package com.jive.qa.dreidel.goyim.views;

import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.inject.Inject;
import javax.management.InstanceNotFoundException;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import lombok.extern.slf4j.Slf4j;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.Maps;
import com.jive.myco.commons.callbacks.CallbackFuture;
import com.jive.qa.dreidel.api.messages.goyim.GoyimServiceResponse;
import com.jive.qa.dreidel.api.messages.goyim.IdResponse;
import com.jive.qa.dreidel.goyim.controllers.JimController;
import com.jive.qa.dreidel.goyim.exceptions.JimCreationException;
import com.jive.qa.dreidel.goyim.exceptions.JimDestructionException;
import com.jive.qa.dreidel.goyim.exceptions.ServiceNotFoundException;
import com.jive.qa.dreidel.goyim.models.InstanceDetails;
import com.jive.qa.dreidel.goyim.service.BmSettings;

@Path("/")
@Slf4j
public class DreidelView
{

  private final BmSettings settings;
  private final JimController jimController;
  private final Map<String, CallbackFuture<Void>> serverCorrelationMap = Maps.newHashMap();

  @Inject
  public DreidelView(final BmSettings settings,
      final JimController jimController)
  {
    this.settings = settings;
    this.jimController = jimController;
  }

  @DELETE
  @Path("/{service}/{id:.*}")
  @Produces(MediaType.APPLICATION_JSON)
  public String deleteServer(@PathParam("service") final String service,
      @PathParam("id") final String id) throws JsonProcessingException, InstanceNotFoundException,
      ServiceNotFoundException, JimDestructionException, InterruptedException, ExecutionException
  {
    log.info("Deleting instance of {} at id: {}", service, id);
    if (!jimController.serviceExists(service))
    {
      throw new ServiceNotFoundException("Service not found");
    }

    if (!jimController.instanceExists(id))
    {
      throw new InstanceNotFoundException("Instance not found");
    }

    jimController.deleteInstance(service, id);
    log.info("Deleted instance of {} at id: {}", service, id);
    return "\"Success\"";
  }

  @POST
  @Path("/{service}/{branch:.*}")
  @Produces(MediaType.APPLICATION_JSON)
  public IdResponse createServerInWorkspace(@PathParam("service") final String service,
      @PathParam("branch") String branch) throws ServiceNotFoundException, JimCreationException,
      JimDestructionException, InterruptedException, ExecutionException
  {
    if (!jimController.serviceExists(service))
    {
      throw new ServiceNotFoundException("Service not found");
    }

    final InstanceDetails details =
        jimController.createInstance(service, settings.getSite(), branch);

    // grab the network that dev can reach

    final String address = details.getNetworks().stream()
        .filter(x -> x.getName().equals(settings.getPreferred())).findFirst().get().getAddress();

    // right before this we need to wait for the server to respond to us.
    final CallbackFuture<Void> callback = new CallbackFuture<>();
    serverCorrelationMap.put(address, callback);

    callback.get();
    log.info("Created new instance of {} at id: {}", details.getService(), details.getId());
    return new IdResponse(details.getRid(), address);
  }

  @POST
  @Path("/servers")
  @Produces(MediaType.APPLICATION_JSON)
  public Response receiveServerNotification(@Context final HttpServletRequest hsr)
  {
    final String address = hsr.getRemoteAddr();

    final CallbackFuture<Void> callback = serverCorrelationMap.get(address);

    callback.onSuccess(null);

    return Response.status(Status.OK).entity(new GoyimServiceResponse("success")).build();
  }
}

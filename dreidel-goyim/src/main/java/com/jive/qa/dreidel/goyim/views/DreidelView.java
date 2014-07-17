package com.jive.qa.dreidel.goyim.views;

import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.inject.Inject;
import javax.inject.Named;
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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jive.myco.commons.callbacks.CallbackFuture;
import com.jive.qa.dreidel.api.messages.goyim.GoyimServiceResponse;
import com.jive.qa.dreidel.api.messages.goyim.IdResponse;
import com.jive.qa.dreidel.goyim.controllers.InstanceManager;
import com.jive.qa.dreidel.goyim.controllers.JimController;
import com.jive.qa.dreidel.goyim.exceptions.JimCreationException;
import com.jive.qa.dreidel.goyim.exceptions.JimDestructionException;
import com.jive.qa.dreidel.goyim.exceptions.ServiceNotFoundException;
import com.jive.qa.dreidel.goyim.jim.JimService;
import com.jive.qa.dreidel.goyim.messages.ErrorMessage;
import com.jive.qa.dreidel.goyim.models.Instance;
import com.jive.qa.dreidel.goyim.service.BmSettings;

@Path("/")
public class DreidelView
{

  private final BmSettings settings;
  private final ObjectMapper json;
  private final InstanceManager instanceManager;
  private final JimController jimController;
  private final JimService jimService;
  private final Map<String, CallbackFuture<Void>> serverCorrelationMap;

  @Inject
  public DreidelView(ObjectMapper json, InstanceManager instanceManager, BmSettings settings,
      JimController jimController, JimService jimService,
      @Named("serverCorrelationMap") Map<String, CallbackFuture<Void>> serverCorrelationMap)
  {
    this.json = json;
    this.instanceManager = instanceManager;
    this.settings = settings;
    this.jimController = jimController;
    this.jimService = jimService;
    this.serverCorrelationMap = serverCorrelationMap;
  }

  @POST
  @Path("/{service}")
  public Response createServer(@PathParam("service") String service)
      throws JsonProcessingException, ServiceNotFoundException, InterruptedException,
      ExecutionException
  {
    if (!jimService.serviceExists(service))
    {
      return Response.status(Status.NOT_FOUND).entity(
          json.writeValueAsString(new ErrorMessage("NonExistantService", "The service "
              + service + " does not exist."))).build();
    }

    Instance instance = instanceManager.getNextAvailableInstance();

    try
    {
      jimController.createInstance(service, instance);

      // grab the network that dev can reach
      String address = instance.getNetworks().stream()
          .filter(x -> x.getName().equals(settings.getPreferred())).findFirst().get().getAddress();

      // right before this we need to wait for the server to respond to us.
      CallbackFuture<Void> callback = new CallbackFuture<>();
      serverCorrelationMap.put(address, callback);

      callback.get();
      // send them the information they need to connect to the server
      return Response
          .status(Status.OK)
          .entity(
              json.writeValueAsString(new IdResponse(instance.getInstance(), address)))
          .build();

    }
    catch (JimCreationException e)
    {
      // we need to remove the instance if there was a problem creating it.
      instanceManager.removeInstance(instance.getInstance());
      // return the error
      // TODO exception mapper
      return Response.status(Status.INTERNAL_SERVER_ERROR).entity(
          json.writeValueAsString(new ErrorMessage(e.getClass().getSimpleName(), e.getMessage())))
          .build();
    }
    catch (JimDestructionException e)
    {
      // don't delete the instance because there is something wrong with deleting it from jim.
      // for now we are going to ignore this problem and just increment.
      // no one will ever delete this instance because no one knows about it therefore it will just
      // be a placeholder.

      // return the error
      // TODO exception mapper
      return Response.status(Status.INTERNAL_SERVER_ERROR).entity(
          json.writeValueAsString(new ErrorMessage(e.getClass().getSimpleName(), e.getMessage())))
          .build();
    }
  }

  @DELETE
  @Path("/{service}/{id}")
  public Response deleteServer(@PathParam("service") String service,
      @PathParam("id") int id) throws JsonProcessingException
  {
    if (!jimService.serviceExists(service + "-dreidel-" + id))
    {
      return Response.status(Status.NOT_FOUND).entity(
          json.writeValueAsString(new ErrorMessage("NonExistantService", "The service "
              + service + " does not exist."))).build();
    }
    if (!jimService.instanceExists(service + "-dreidel-" + id, id, settings.getSite()))
    {
      return Response.status(Status.NOT_FOUND).entity(
          json.writeValueAsString(new ErrorMessage("NonExistantInstance", "The Instance "
              + id + " does not exist."))).build();
    }
    try
    {
      // first remove it from jim
      jimController.deleteInstance(service, id);
      // if there wasn't an exception remove it from the instance manager.
      instanceManager.removeInstance(id);
      return Response.status(Status.OK).build();
    }
    catch (JimDestructionException e)
    {
      // TODO exception mapper
      return Response
          .status(Status.INTERNAL_SERVER_ERROR)
          .entity(
              json.writeValueAsString(new ErrorMessage(e.getClass().getSimpleName(), e.getMessage())))
          .build();
    }
  }

  @POST
  @Path("/servers")
  @Produces(MediaType.APPLICATION_JSON)
  public Response receiveServerNotification(@Context HttpServletRequest hsr)
  {
    String address = hsr.getRemoteAddr();

    CallbackFuture<Void> callback = serverCorrelationMap.get(address);

    callback.onSuccess(null);

    return Response.status(Status.OK).entity(new GoyimServiceResponse("success")).build();
  }
}

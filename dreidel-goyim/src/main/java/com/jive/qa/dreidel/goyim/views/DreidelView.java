package com.jive.qa.dreidel.goyim.views;

import java.io.IOException;
import java.net.MalformedURLException;

import javax.inject.Inject;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import lombok.extern.slf4j.Slf4j;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jive.qa.dreidel.goyim.controllers.InstanceManager;
import com.jive.qa.dreidel.goyim.controllers.JimController;
import com.jive.qa.dreidel.goyim.exceptions.JimCreationException;
import com.jive.qa.dreidel.goyim.exceptions.JimDestructionException;
import com.jive.qa.dreidel.goyim.messages.ErrorMessage;
import com.jive.qa.dreidel.goyim.models.Instance;
import com.jive.qa.dreidel.goyim.service.BmSettings;

@Path("/")
@Slf4j
public class DreidelView
{

  private final BmSettings settings;
  private final ObjectMapper json;
  private final InstanceManager instanceManager;
  private final JimController jimController;

  @Inject
  public DreidelView(ObjectMapper json, InstanceManager instanceManager, BmSettings settings,
      JimController jimController)
  {
    this.json = json;
    this.instanceManager = instanceManager;
    this.settings = settings;
    this.jimController = jimController;
  }

  @POST
  @Path("/{service}")
  public Response createServer(@PathParam("service") String service) throws JsonProcessingException
  {
    if (!jimController.serviceExists(service))
    {
      return Response.status(Status.NOT_FOUND).entity(
          json.writeValueAsString(new ErrorMessage("NonExistantService", "The service "
              + service + " does not exist."))).build();
    }

    Instance instance = instanceManager.getNextAvailableInstance();

    try
    {
      jimController.createInstance(service, instance);
      // TODO return id of the instance
      return Response.status(Status.OK).build();
    }
    catch (JimCreationException e)
    {
      return Response.status(Status.INTERNAL_SERVER_ERROR).entity(
          json.writeValueAsString(new ErrorMessage(e.getClass().getSimpleName(), e.getMessage())))
          .build();
    }
  }

  @DELETE
  @Path("/{service}/{id}")
  public Response deleteServer(@PathParam("service") String service,
      @PathParam("id") int id) throws MalformedURLException, IOException
  {
    try
    {
      jimController.deleteInstance(service, id, settings.getSite());
      instanceManager.removeInstance(id);
      return Response.status(Status.OK).build();
    }
    catch (JimDestructionException e)
    {
      return Response
          .status(Status.INTERNAL_SERVER_ERROR)
          .entity(
              json.writeValueAsString(new ErrorMessage(e.getClass().getSimpleName(), e.getMessage())))
          .build();
    }
  }
}

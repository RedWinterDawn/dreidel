package com.jive.qa.dreidel.spinnit.jinst;

import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import com.google.common.util.concurrent.ListenableFuture;

public interface GoyimJinstResource
{

  @POST
  @Path("/properties/{path}")
  ListenableFuture<Map<String, String>> setProperties(Map<String, String> properties,
      @PathParam("path") String pathToFile);

  @PUT
  @Path("/service/{serviceName}")
  ListenableFuture<String> restartService(
      @PathParam("serviceName") String serviceName);

  @GET
  @Path("/service/{serviceName}")
  ListenableFuture<String> getServiceStatus(@PathParam("serviceName") String serviceName);

}

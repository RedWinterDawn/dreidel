package com.jive.qa.dreidel.goyim.rest;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import com.google.common.util.concurrent.ListenableFuture;
import com.jive.qa.dreidel.goyim.models.InstanceDetails;
import com.jive.qa.dreidel.goyim.models.Instance;
import com.jive.qa.dreidel.goyim.models.ServiceDetail;

@Path("/api")
public interface JimResource
{
  @GET
  @Path("services/{serviceName}")
  ListenableFuture<ServiceDetail> getService(@PathParam("serviceName") final String serviceName,
      @HeaderParam("Authorization") String authorization);

  @POST
  @Path("/services/")
  ListenableFuture<Void> createService(final ServiceDetail details,
      @HeaderParam("Authorization") String authorization);

  @POST
  @Path("/instances")
  ListenableFuture<InstanceDetails> createInstance(final Instance instance,
      @HeaderParam("Authorization") String authorization);

  @POST
  @Path("/instances/{instanceId}/_rebuild")
  ListenableFuture<Void> bootInstance(@PathParam("instanceId") final String instanceId,
      @HeaderParam("Authorization") String authorization);

  @DELETE
  @Path("/instances/{instanceId}")
  ListenableFuture<Void> deleteInstance(@PathParam("instanceId") final String instanceId,
      @HeaderParam("Authorization") String authorization);

  @DELETE
  @Path("/services/{service}")
  ListenableFuture<Void> deleteService(@PathParam("service") final String service,
      @HeaderParam("Authorization") String authorization);

  @GET
  @Path("/instances/{instanceId}")
  ListenableFuture<InstanceDetails> getInstance(@PathParam("instanceId") String instanceId,
      @HeaderParam("Authorization") String authorization);

}

package com.jive.qa.dreidel.goyim.rest;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import com.google.common.util.concurrent.ListenableFuture;
import com.jive.qa.dreidel.goyim.models.InstanceDetails;
import com.jive.qa.dreidel.goyim.models.NewInstance;
import com.jive.qa.dreidel.goyim.models.ServiceDetail;

@Path("/api")
public interface JimResource
{
  @GET
  @Path("/services/{serviceName}")
  ListenableFuture<ServiceDetail> getService(@PathParam("serviceName") final String serviceName);

  @POST
  @Path("/services/")
  ListenableFuture<Void> createService(final ServiceDetail details);

  @POST
  @Path("/instances")
  ListenableFuture<InstanceDetails> createInstance(final NewInstance instance);

  @POST
  @Path("/instances/{instanceId}/_rebuild")
  ListenableFuture<Void> bootInstance(@PathParam("instanceId") final String instanceId);

  @DELETE
  @Path("/instances/{instanceId}")
  ListenableFuture<Void> deleteInstance(final String instanceId);

  @DELETE
  @Path("/services/{service}")
  ListenableFuture<Void> deleteService(final String service);

  @GET
  @Path("/instances/{instanceId}")
  ListenableFuture<Void> getInstance(final String instanceId);


}

package com.jive.qa.dreidel.goyim.jinst.service.postback;

import javax.ws.rs.POST;
import javax.ws.rs.Path;

import com.google.common.util.concurrent.ListenableFuture;
import com.jive.qa.dreidel.api.messages.goyim.GoyimServiceResponse;

public interface GoyimServiceResource
{
  @Path("/servers")
  @POST
  ListenableFuture<GoyimServiceResponse> updateService();

}

package com.jive.qa.dreidel.goyim.rest;

import lombok.AllArgsConstructor;

import com.google.common.util.concurrent.ListenableFuture;
import com.jive.qa.dreidel.goyim.models.InstanceDetails;
import com.jive.qa.dreidel.goyim.models.Instance;
import com.jive.qa.dreidel.goyim.models.ServiceDetail;

@AllArgsConstructor
public class JimService
{

  private final JimResource endpoint;
  private final String token;
  private static final String TOKEN_PREFIX = "Token  token=";

  private String getTokenValue()
  {
    return TOKEN_PREFIX + token;
  }

  public ListenableFuture<ServiceDetail> getService(String service)
  {
    return endpoint.getService(service, getTokenValue());
  }

  public ListenableFuture<Void> createService(ServiceDetail details)
  {
    return endpoint.createService(details, getTokenValue());
  }

  public ListenableFuture<InstanceDetails> createInstance(Instance instance)
  {
    return endpoint.createInstance(instance, getTokenValue());
  }

  public ListenableFuture<Void> bootInstance(String instanceId)
  {
    return endpoint.bootInstance(instanceId, getTokenValue());
  }

  public ListenableFuture<Void> deleteInstance(String instanceId)
  {
    return endpoint.deleteInstance(instanceId, getTokenValue());
  }

  public ListenableFuture<Void> deleteService(String service)
  {
    return endpoint.deleteService(service, getTokenValue());
  }

  public ListenableFuture<InstanceDetails> getInstance(final String instanceId)
  {
    return endpoint.getInstance(instanceId, getTokenValue());
  }

}

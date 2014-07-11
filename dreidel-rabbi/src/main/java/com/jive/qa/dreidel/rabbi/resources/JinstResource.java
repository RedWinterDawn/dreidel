package com.jive.qa.dreidel.rabbi.resources;

import java.net.URL;

import lombok.Getter;

import com.google.common.net.HostAndPort;
import com.jive.qa.dreidel.api.exceptions.ResourceDestructionException;
import com.jive.qa.dreidel.api.exceptions.ResourceInitializationException;
import com.jive.qa.dreidel.api.messages.goyim.IdResponse;
import com.jive.qa.dreidel.api.messages.goyim.ResponseCodeOnly;
import com.jive.qa.restinator.Endpoint;

public class JinstResource extends BaseResourceImpl implements BaseResource
{

  private final String jClass;
  private final URL creatorUrl;
  private final Endpoint<IdResponse, Void> instanceCreator;
  private final Endpoint<ResponseCodeOnly, Void> instanceDestroyer;

  @Getter
  private int goyimId;
  @Getter
  private String ip;

  public JinstResource(String jClass, URL creatorUrl, Endpoint<IdResponse, Void> instanceCreator,
      Endpoint<ResponseCodeOnly, Void> instanceDestroyer)
  {
    this.jClass = jClass;
    this.creatorUrl = creatorUrl;
    this.instanceCreator = instanceCreator;
    this.instanceDestroyer = instanceDestroyer;
  }

  @Override
  public void init() throws ResourceInitializationException
  {
    try
    {
      IdResponse response = instanceCreator.url(creatorUrl, "/" + jClass).post();
      this.goyimId = response.getId();
      this.hap = HostAndPort.fromParts(response.getAddress(), 0);
    }
    catch (Exception e)
    {
      throw new ResourceInitializationException(
          "There was something wrong with communicating with the goyim", e);
    }
  }

  @Override
  public void destroy() throws ResourceDestructionException
  {
    try
    {
      ResponseCodeOnly response =
          instanceDestroyer.url(creatorUrl, "/" + jClass + "/" + this.goyimId).delete();
    }
    catch (Exception e)
    {
      throw new ResourceDestructionException(
          "There was something wrong with communicating with the goyim", e);
    }
  }

}

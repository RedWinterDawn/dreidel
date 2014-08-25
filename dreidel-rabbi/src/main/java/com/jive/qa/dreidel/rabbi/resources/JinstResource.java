package com.jive.qa.dreidel.rabbi.resources;

import java.net.URL;

import lombok.Getter;
import lombok.ToString;

import com.google.common.net.HostAndPort;
import com.jive.qa.dreidel.api.exceptions.ResourceDestructionException;
import com.jive.qa.dreidel.api.exceptions.ResourceInitializationException;
import com.jive.qa.dreidel.api.messages.goyim.IdResponse;
import com.jive.qa.dreidel.api.messages.goyim.ResponseCodeOnly;
import com.jive.qa.restinator.Endpoint;

@ToString
public class JinstResource extends BaseResourceImpl implements BaseResource
{

  private final String jClass;
  private final URL creatorUrl;
  private final Endpoint<IdResponse, Void> instanceCreator;
  private final Endpoint<ResponseCodeOnly, Void> instanceDestroyer;
  private final String branch;

  @Getter
  private String goyimId;
  @Getter
  private String ip;

  public JinstResource(String jClass, URL creatorUrl, Endpoint<IdResponse, Void> instanceCreator,
      Endpoint<ResponseCodeOnly, Void> instanceDestroyer, String branch)
  {
    this.jClass = jClass;
    this.creatorUrl = creatorUrl;
    this.instanceCreator = instanceCreator;
    this.instanceDestroyer = instanceDestroyer;
    this.branch = branch;
  }

  @Override
  public void init() throws ResourceInitializationException
  {
    try
    {
      IdResponse response = instanceCreator.url(creatorUrl, "/" + jClass + "/" + branch).post();
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
      instanceDestroyer.url(creatorUrl, "/" + jClass + "/" + this.goyimId).delete();
    }
    catch (Exception e)
    {
      throw new ResourceDestructionException(
          "There was something wrong with communicating with the goyim", e);
    }
  }

}

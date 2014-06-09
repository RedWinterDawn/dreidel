package com.jive.qa.dreidel.rabbi.resources;

import com.google.common.net.HostAndPort;
import com.jive.qa.dreidel.api.exceptions.ResourceDestructionException;
import com.jive.qa.dreidel.api.exceptions.ResourceInitializationException;
import com.jive.qa.dreidel.api.messages.ResourceId;

/**
 * The base resource that all resources must implement.
 * 
 * @author jdavidson
 *
 */
public interface BaseResource
{
  /**
   * 
   * @return returns the id of the resource
   */
  public ResourceId getId();

  /**
   * 
   * @return gets the host of the resource
   */
  public HostAndPort getHap();

  /**
   * spins up the resource and returns when the resource is finished spinning up
   * 
   * @throws ResourceInitializationException
   *           if there is a problem with the initialization
   */
  public void init() throws ResourceInitializationException;

  /**
   * tears down the resource and returns when the resource is finished being destroyed
   * 
   * @throws ResourceDestructionException
   *           if there is a problem with the destruction
   */
  public void destroy() throws ResourceDestructionException;
}

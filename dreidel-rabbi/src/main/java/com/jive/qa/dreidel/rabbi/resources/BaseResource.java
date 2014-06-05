package com.jive.qa.dreidel.rabbi.resources;

import com.jive.qa.dreidel.api.exceptions.ResourceDestructionException;
import com.jive.qa.dreidel.api.exceptions.ResourceInitializationException;

public interface BaseResource
{
  public String getId();

  public String getHost();

  public int getPort();

  public void init() throws ResourceInitializationException;

  public void destroy() throws ResourceDestructionException;
}

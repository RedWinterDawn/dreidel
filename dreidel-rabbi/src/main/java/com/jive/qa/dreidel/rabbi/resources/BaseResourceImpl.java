package com.jive.qa.dreidel.rabbi.resources;

import lombok.Getter;
import lombok.NonNull;

import org.apache.commons.lang.RandomStringUtils;

/**
 * 
 * @author jdavidson
 *
 */
@Getter
public abstract class BaseResourceImpl implements BaseResource
{
  private final String id;
  private final int idLength = 20;
  private final String host;
  private final int port;

  /**
   * 
   * @param host the host of the resource
   * 
   * @param port2 the port of the resource
   */
  BaseResourceImpl(final @NonNull String host, final int port2)
  {
    id = RandomStringUtils.randomAlphabetic(idLength).toLowerCase();
    port = port2;
    this.host = host;
  }
}

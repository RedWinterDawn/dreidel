package com.jive.qa.dreidel.rabbi.resources;

import lombok.Getter;
import lombok.NonNull;

import org.apache.commons.lang.RandomStringUtils;

import com.google.common.net.HostAndPort;
import com.jive.qa.dreidel.api.messages.ResourceId;

/**
 * 
 * @author jdavidson
 *
 */
@Getter
public abstract class BaseResourceImpl implements BaseResource
{
  private final ResourceId id;
  private final int idLength = 20;
  protected HostAndPort hap;

  /**
   * 
   * @param host the host of the resource
   * 
   * @param port2 the port of the resource
   */
  public BaseResourceImpl(final @NonNull HostAndPort hap)
  {
    id = ResourceId.valueOf(RandomStringUtils.randomAlphabetic(idLength).toLowerCase());
    this.hap = hap;
  }
  
  public BaseResourceImpl() {
    id = ResourceId.valueOf(RandomStringUtils.randomAlphabetic(idLength).toLowerCase());
  }
  
}

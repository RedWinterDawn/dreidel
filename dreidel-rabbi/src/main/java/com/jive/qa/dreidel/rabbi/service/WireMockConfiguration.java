package com.jive.qa.dreidel.rabbi.service;

import lombok.Getter;

import org.eclipse.jetty.util.annotation.Name;

import com.google.inject.Inject;

@Getter
public class WireMockConfiguration
{

  private final String host;

  @Inject
  public WireMockConfiguration(@Name("wiremock.host") String host)
  {
    this.host = host;
  }
}

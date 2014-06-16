package com.jive.qa.dreidel.goyim.service;

import javax.inject.Named;

import lombok.Getter;

import com.google.inject.Inject;

@Getter
public class JimSettings
{

  @Inject
  public JimSettings(@Named("jim.key") String key, @Named("jim.ip") String ip,
      @Named("jim.port") int port, @Named("jim.ep") String ep)
  {
    this.key = key;
    this.ip = ip;
    this.port = port;
    this.ep = ep;
  }

  private final String key;
  private final String ip;
  private final int port;
  private final String ep;

}

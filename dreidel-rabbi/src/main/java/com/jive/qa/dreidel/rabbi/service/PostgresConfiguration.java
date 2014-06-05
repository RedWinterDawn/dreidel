package com.jive.qa.dreidel.rabbi.service;

import lombok.Getter;

import com.google.common.net.HostAndPort;
import com.google.inject.Inject;
import com.google.inject.name.Named;

@Getter
public class PostgresConfiguration
{

  private final HostAndPort hap;
  private final String username;
  private final String password;

  @Inject
  public PostgresConfiguration(@Named("postgresPort") final int port,
      @Named("postgresHost") final String host, @Named("postgresUsername") final String username,
      @Named("postgresPassword") final String password)
  {
    hap = HostAndPort.fromParts(host, port);
    this.username = username;
    this.password = password;
  }

}

package com.jive.qa.dreidel.rabbi.models;

import java.net.SocketException;

import lombok.Getter;
import lombok.NonNull;

import org.apache.commons.lang.RandomStringUtils;

import com.jive.myco.commons.io.AvailablePortFinder;

@Getter
public class BaseModel
{
  private final String name;
  private final String host;
  private final int port;
  private final int length = 20;

  BaseModel(final @NonNull String host, final int port2)
  {
    name = RandomStringUtils.randomAlphabetic(length).toLowerCase();
    port = port2;
    this.host = host;
  }

  BaseModel(final @NonNull String host) throws SocketException
  {
    name = RandomStringUtils.randomAlphabetic(length).toLowerCase();
    port = AvailablePortFinder.getNextAvailable();
    this.host = host;
  }
}

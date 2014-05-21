package com.jive.qa.dreidel.goyim.service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

/**
 * An example of a lifecycled entity used in a Guice Injector to demonstrate lifecycle ordering
 * between dependeny bindings in the Injector.
 *
 * @author David Valeri
 */
@Singleton
@Slf4j
@Getter
public class ExampleResource
{
  @NonNull
  private final String arg0;

  @NonNull
  private final String arg1;

  private volatile boolean running;

  @Inject
  public ExampleResource(
      @Named("arg0") final String arg0,
      @Named("arg1") final String arg1)
  {
    this.arg0 = arg0;
    this.arg1 = arg1;
  }

  @PostConstruct
  public void init() {
    running = true;
    log.info("Started ExampleResource [{}].", this);
  }

  @PreDestroy
  public void destroy() {
    running = false;
    log.info("Stopped ExampleResource [{}].", this);
  }
}

package com.jive.qa.dreidel.goyim.service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;

import lombok.extern.slf4j.Slf4j;

/**
 * An example service class demonstrating injection and the use of annotations to control lifecycle.
 * 
 * @author David Valeri
 */
@Slf4j
public class ExampleService
{
  private final long frequency;
  private final ExampleResource exampleResource;
  
  private volatile boolean run;
  private volatile Thread t;

  @Inject
  public ExampleService(
      final ExampleResource exampleResource,
      @Named("frequency") final long frequency)
  {
    this.exampleResource = exampleResource;
    this.frequency = frequency;
  }

  @PostConstruct
  public void start()
  {
    run = true;

    t = new Thread()
    {
      @Override
      public void run()
      {
        while (run && !isInterrupted())
        {
          if (exampleResource.isRunning())
          {
            log.info("{} {}", exampleResource.getArg0(), exampleResource.getArg1());
          }
          else
          {
            log.warn("Example resource is not running.");
          }

          try
          {
            Thread.sleep(frequency);
          }
          catch (final InterruptedException e)
          {
            // Ignore
          }
        }

      }
    };

    t.start();
  }

  @PreDestroy
  public void stop()
  {
    log.info("Shutting down.");

    run = false;
    try
    {
      t.join(5000);
      if (t.isAlive())
      {
        log.error("Failed to shutdown correctly.");
      }
      log.info("Shut down complete.");
    }
    catch (final InterruptedException e)
    {
      // Ignore
    }
  }
}

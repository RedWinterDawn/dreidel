package com.jive.qa.dreidel.goyim.Integration;

import static org.junit.Assert.assertEquals;

import java.net.URL;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.junit.Test;

import com.jive.qa.restinator.Endpoint;
import com.jive.qa.restinator.codecs.ByteArrayEndpointCodec;

@Slf4j
public class DreidelView_Concurrency_Int extends StartServer
{

  @Test
  public void test() throws Exception
  {
    Endpoint<String, String> ep =
        new Endpoint<String, String>(new ByteArrayEndpointCodec<String, String>()
        {

          @Override
          public byte[] encode(String object)
          {
            return object.getBytes();
          }

          @Override
          public String decode(byte[] bytes, int responseCode)
          {
            return new String(bytes);
          }
        });

    URL base = new URL("http://localhost:8019/");

    AtomicInteger inserts = new AtomicInteger();
    AtomicInteger deletes = new AtomicInteger();
    AtomicInteger badThings = new AtomicInteger();

    ScheduledExecutorService executor = Executors.newScheduledThreadPool(Integer.MAX_VALUE);

    int testCount = 100;

    for (int i = 0; i < testCount; i++)
    {
      executor.schedule(new InsertRunner(inserts, badThings, ep, base), new Random().nextInt(10),
          TimeUnit.MILLISECONDS);
      Thread.sleep(1);
    }

    while (inserts.get() != testCount)
    {
      Thread.sleep(100);
      log.debug("#Created {} instances", inserts.get());
      assertEquals(0, badThings.get());
    }

    Thread.sleep(1000);

    for (int i = 0; i < testCount; i++)
    {
      executor.schedule(new DeleteRunner(deletes, badThings, ep, base, i),
          new Random().nextInt(10), TimeUnit.MILLISECONDS);
      Thread.sleep(1);
    }

    while (deletes.get() != testCount)
    {
      Thread.sleep(100);
      log.debug("Deleted {} instances", deletes.get());
      assertEquals(0, badThings.get());
    }
  }

  @AllArgsConstructor
  public class InsertRunner implements Runnable
  {
    AtomicInteger inserts;
    AtomicInteger badThings;
    Endpoint<String, String> ep;
    URL base;

    @Override
    public void run()
    {
      try
      {

        String response = ep.url(base, "test-1").post();
        if (!response.contains("id"))
        {
          log.error("something happened {}", response);
          badThings.incrementAndGet();
        }
        else
        {
          log.debug("inserted {}", response);
          inserts.incrementAndGet();
        }
      }
      catch (Exception e)
      {
        badThings.incrementAndGet();
      }

    }

  }

  @AllArgsConstructor
  public class DeleteRunner implements Runnable
  {
    AtomicInteger deletes;
    AtomicInteger badThings;
    Endpoint<String, String> ep;
    URL base;
    Integer instance;

    @Override
    public void run()
    {
      try
      {
        String response = ep.url(base, "test-1/" + instance).delete();
        if (!response.equals(""))
        {
          log.error("something happened {}", response);
          badThings.incrementAndGet();
        }
        else
        {
          log.debug("deleted {}", response);
          deletes.incrementAndGet();
        }
      }
      catch (Exception e)
      {
        badThings.incrementAndGet();
      }

    }
  }

}

package com.jive.qa.dreidel.goyim.Integration;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

import com.jive.qa.restinator.Endpoint;
import com.jive.qa.restinator.codecs.ByteArrayEndpointCodec;

public class DreidelView_FullIntegration_Int extends StartServer
{
  @Test
  public void test() throws MalformedURLException, IOException
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

    // insert 10 things and don't delete them
    // we should still be able to insert on this instance
    // these will act like stray instances that didn't get cleaned up.
    for (int i = 0; i < 10; i++)
    {
      String response = ep.url(base, "test-1").post();

      if (!response.contains("id"))
      {
        badThings.incrementAndGet();
      }
      else
      {
        inserts.incrementAndGet();
      }
    }

    for (int i = 0; i < 100; i++)
    {
      String response = ep.url(base, "test-1").post();

      if (!response.contains("id"))
      {
        badThings.incrementAndGet();
      }
      else
      {
        inserts.incrementAndGet();
      }
    }

    for (int i = 0; i < 110; i++)
    {
      String response = ep.url(base, "test-1/" + i).delete();
      if (!response.equals(""))
      {
        badThings.incrementAndGet();
      }
      else
      {
        deletes.incrementAndGet();
      }

    }

    assertEquals(110, inserts.get());
    assertEquals(110, deletes.get());
    assertEquals(0, badThings.get());

  }
}

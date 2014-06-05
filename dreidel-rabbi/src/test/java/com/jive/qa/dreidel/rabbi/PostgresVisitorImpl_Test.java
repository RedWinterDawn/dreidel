package com.jive.qa.dreidel.rabbi;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.net.HostAndPort;
import com.jive.myco.commons.callbacks.Callback;
import com.jive.myco.commons.callbacks.CallbackFuture;
import com.jive.qa.dreidel.api.exceptions.ResourceInitializationException;
import com.jive.qa.dreidel.api.messages.VisitorContext;
import com.jive.qa.dreidel.api.messages.postgres.PostgresCreateMessage;
import com.jive.qa.dreidel.api.messages.postgres.PostgresExecSqlMessage;
import com.jive.qa.dreidel.api.replies.ConnectionInformationReply;
import com.jive.qa.dreidel.api.replies.Reply;
import com.jive.qa.dreidel.rabbi.resources.BaseResource;
import com.jive.qa.dreidel.rabbi.resources.PostgresResource;
import com.jive.qa.dreidel.rabbi.resources.ResourceFactory;
import com.jive.qa.dreidel.rabbi.service.PostgresConfiguration;
import com.jive.qa.dreidel.rabbi.views.PostgresVisitorImpl;

public class PostgresVisitorImpl_Test
{

  private PostgresVisitorImpl visitor;
  private final PostgresConfiguration config = new PostgresConfiguration(5432, "localhost",
      "postgres", "");
  private Map<String, List<BaseResource>> correlationMap;

  PostgresResource resource = mock(PostgresResource.class);

  private final ResourceFactory factory = new ResourceFactory()
  {

    @Override
    public PostgresResource getPostgresResource(final HostAndPort hap, final String username,
        final String password)
    {
      return resource;
    }

  };
  private final String TEST_ID = "SDFSD";

  @Before
  public void setup()
  {
    correlationMap = Maps.newHashMap();
    correlationMap.put(TEST_ID, Lists.newArrayList());
    visitor = new PostgresVisitorImpl(config, correlationMap, factory);
  }

  @Test(timeout = 500)
  public void PostgresCreateMessage_CreatesNewDatabase() throws Exception
  {
    AtomicInteger badThings = new AtomicInteger();
    AtomicInteger goodThings = new AtomicInteger();

    visitor.visit(new PostgresCreateMessage("asdfasdf"), new VisitorContext(TEST_ID)).addCallback(
        new Callback<Reply>()
        {

          @Override
          public void onSuccess(final Reply result)
          {
            goodThings.incrementAndGet();
          }

          @Override
          public void onFailure(final Throwable cause)
          {
            badThings.incrementAndGet();
          }
        });

    while (goodThings.get() != 1)
    {
      Thread.sleep(1);
    }

    assertEquals(goodThings.get(), 1);
    assertEquals(badThings.get(), 0);

  }

  @Test(timeout = 100)
  public void PostgresCreateMessage_UnregisteredTest_FailsCallback() throws Exception
  {
    AtomicInteger badThings = new AtomicInteger();
    AtomicInteger goodThings = new AtomicInteger();

    visitor.visit(new PostgresCreateMessage("asdfasdf"), new VisitorContext("asdfasdf"))
        .addCallback(
            new Callback<Reply>()
            {

              @Override
              public void onSuccess(final Reply result)
              {
                badThings.incrementAndGet();
              }

              @Override
              public void onFailure(final Throwable cause)
              {
                goodThings.incrementAndGet();
              }
            });

    while (goodThings.get() != 1)
    {
      Thread.sleep(1);
    }

    assertEquals(goodThings.get(), 1);
    assertEquals(badThings.get(), 0);
  }

  @Test(timeout = 100)
  public void PostgresCreateMessage_FailToCreateDatabase_FailsCallback() throws Exception
  {
    AtomicInteger badThings = new AtomicInteger();
    AtomicInteger goodThings = new AtomicInteger();

    // make the resource fail on init;

    Mockito.doThrow(new ResourceInitializationException()).when(resource).init();

    visitor.visit(new PostgresCreateMessage("asdfasdf"), new VisitorContext(TEST_ID))
        .addCallback(
            new Callback<Reply>()
            {

              @Override
              public void onSuccess(final Reply result)
              {
                badThings.incrementAndGet();
              }

              @Override
              public void onFailure(final Throwable cause)
              {
                if (cause instanceof ResourceInitializationException)
                {
                  goodThings.incrementAndGet();
                }
                else
                {
                  badThings.incrementAndGet();
                }
              }
            });

    while (goodThings.get() != 1)
    {
      Thread.sleep(1);
    }

    assertEquals(goodThings.get(), 1);
    assertEquals(badThings.get(), 0);
  }

  @Test
  public void PostgresExecuteSql_NoDatabaseRegistered_Fails() throws Exception
  {
    AtomicInteger badThings = new AtomicInteger();
    AtomicInteger goodThings = new AtomicInteger();

    visitor.visit(new PostgresExecSqlMessage("asdf", "create table foo()", "asdfa"),
        new VisitorContext(TEST_ID)).addCallback(new Callback<Reply>()
    {

      @Override
      public void onSuccess(final Reply result)
      {
        badThings.incrementAndGet();
      }

      @Override
      public void onFailure(final Throwable cause)
      {
        if (cause instanceof NullPointerException)
        {
          goodThings.incrementAndGet();
        }
        else
        {
          badThings.incrementAndGet();
        }
      }
    });

    while (goodThings.get() != 1)
    {
      Thread.sleep(1);
    }

    assertEquals(goodThings.get(), 1);
    assertEquals(badThings.get(), 0);
  }

  @Test
  public void PostgresExecuteSql_DatabaseRegistered_Fails() throws Exception
  {
    AtomicInteger badThings = new AtomicInteger();
    AtomicInteger goodThings = new AtomicInteger();

    ConnectionInformationReply reply = createDatabase();

    when(resource.getId()).thenReturn("asdfas234234d");

    visitor.visit(
        new PostgresExecSqlMessage("asdf", "create table foo()", reply.getConnections().get(0)
            .getId()),
        new VisitorContext(TEST_ID)).addCallback(new Callback<Reply>()
    {

      @Override
      public void onSuccess(final Reply result)
      {
        badThings.incrementAndGet();
      }

      @Override
      public void onFailure(final Throwable cause)
      {
        if (cause instanceof NullPointerException)
        {
          goodThings.incrementAndGet();
        }
        else
        {
          badThings.incrementAndGet();
        }
      }
    });

    while (goodThings.get() != 1)
    {
      Thread.sleep(1);
    }

    assertEquals(goodThings.get(), 1);
    assertEquals(badThings.get(), 0);
  }

  public ConnectionInformationReply createDatabase() throws InterruptedException,
      ExecutionException
  {
    CallbackFuture<Reply> callback = new CallbackFuture<>();
    visitor.visit(new PostgresCreateMessage("asdfasdf"), new VisitorContext(TEST_ID)).addCallback(
        callback);
    return (ConnectionInformationReply) callback.get();

  }

}

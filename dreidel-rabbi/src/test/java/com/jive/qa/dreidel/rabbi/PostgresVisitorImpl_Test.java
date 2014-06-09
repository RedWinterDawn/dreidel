package com.jive.qa.dreidel.rabbi;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
import com.jive.myco.commons.concurrent.Pnky;
import com.jive.myco.commons.concurrent.PnkyPromise;
import com.jive.qa.dreidel.api.exceptions.ResourceInitializationException;
import com.jive.qa.dreidel.api.messages.ResourceId;
import com.jive.qa.dreidel.api.messages.VisitorContext;
import com.jive.qa.dreidel.api.messages.postgres.PostgresCreateMessage;
import com.jive.qa.dreidel.api.messages.postgres.PostgresExecSqlMessage;
import com.jive.qa.dreidel.api.replies.ConnectionInformationReply;
import com.jive.qa.dreidel.api.replies.Reply;
import com.jive.qa.dreidel.rabbi.resources.BaseResource;
import com.jive.qa.dreidel.rabbi.resources.PostgresResource;
import com.jive.qa.dreidel.rabbi.resources.ResourceFactory;
import com.jive.qa.dreidel.rabbi.service.PostgresConfiguration;
import com.jive.qa.dreidel.rabbi.visitors.PostgresVisitorImpl;

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

    visitor.visit(new PostgresCreateMessage("asdfasdf"), new VisitorContext(TEST_ID))
        .thenCompose((result) -> {
          Pnky<Void> future = Pnky.create();
          goodThings.incrementAndGet();
          future.resolve(null);
          return future;
        }).onFailure((cause) -> {
          badThings.incrementAndGet();
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
        .thenCompose((result) -> {
          Pnky<Void> future = Pnky.create();
          badThings.incrementAndGet();
          future.resolve(null);
          return future;
        }).onFailure((cause) -> {
          goodThings.incrementAndGet();
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
        .thenCompose((result) -> {
          Pnky<Void> future = Pnky.create();
          badThings.incrementAndGet();
          future.resolve(null);
          return future;
        }).onFailure((cause) -> {
          if (cause instanceof ResourceInitializationException)
          {
            goodThings.incrementAndGet();
          }
            else
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
  public void PostgresExecuteSql_NoDatabaseRegistered_Fails() throws Exception
  {
    AtomicInteger badThings = new AtomicInteger();
    AtomicInteger goodThings = new AtomicInteger();

    visitor
        .visit(
            new PostgresExecSqlMessage("asdf", ResourceId.valueOf("asdfa"), "create table foo()"),
            new VisitorContext(TEST_ID)).thenCompose((result) -> {
          Pnky<Void> future = Pnky.create();
          badThings.incrementAndGet();
          future.resolve(null);
          return future;
        }).onFailure((cause) -> {
          if (cause instanceof NullPointerException)
          {
            goodThings.incrementAndGet();
          }
            else
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
  public void PostgresExecuteSql_DatabaseRegistered_works() throws Exception
  {
    AtomicInteger badThings = new AtomicInteger();
    AtomicInteger goodThings = new AtomicInteger();

    when(resource.getId()).thenReturn(ResourceId.valueOf("asdfas234234d"));

    ConnectionInformationReply reply = createDatabase();

    visitor.visit(
        new PostgresExecSqlMessage("asdf", reply.getConnections().get(0)
            .getId(), "create table foo()"),
        new VisitorContext(TEST_ID)).thenCompose((result) -> {
      Pnky<Void> future = Pnky.create();
      goodThings.incrementAndGet();
      future.resolve(null);
      return future;
    }).onFailure((cause) -> {
      badThings.incrementAndGet();
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
    PnkyPromise<Reply> promise =
        visitor.visit(new PostgresCreateMessage("asdfasdf"), new VisitorContext(TEST_ID));
    return (ConnectionInformationReply) promise.get();

  }
}

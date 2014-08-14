package com.jive.qa.dreidel.rabbi;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Before;
import org.junit.Test;

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
import com.jive.qa.dreidel.rabbi.resources.JinstResource;
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

    @Override
    public JinstResource getJinstResource(String jClass, String branch)
    {
      // TODO Auto-generated method stub
      return null;
    }

  };
  private final String TEST_ID = "SDFSD";

  @Before
  public void setup()
  {
    when(resource.getHap()).thenReturn(HostAndPort.fromParts("localhost", 8321));
    correlationMap = Maps.newHashMap();
    correlationMap.put(TEST_ID, Lists.newArrayList());
    visitor = new PostgresVisitorImpl(config, correlationMap, factory);
  }

  @Test(timeout = 500)
  public void PostgresCreateMessage_CreatesNewDatabase() throws Exception
  {
    AtomicInteger badThings = new AtomicInteger();
    AtomicInteger goodThings = new AtomicInteger();

    when(resource.getId()).thenReturn(ResourceId.valueOf("asdfas234234d"));
    // make sure init was called
    doAnswer((invocation) ->
    {
      goodThings.incrementAndGet();
      return invocation;

    }).when(resource).init();

    try
    {
      visitor.visit(new PostgresCreateMessage("asdfasdf"), new VisitorContext(TEST_ID))
          .thenCompose((result) ->
          {
            if (result instanceof ConnectionInformationReply)
            {
              goodThings.incrementAndGet();
            }
              else
              {
                badThings.incrementAndGet();
              }
              return Pnky.immediatelyComplete(null);
            }).get();
    }
    catch (Exception ex)
    {
      badThings.incrementAndGet();
    }

    assertEquals(goodThings.get(), 2);
    assertEquals(badThings.get(), 0);

  }

  @Test(timeout = 100)
  public void PostgresCreateMessage_UnregisteredTest_FailsCallback() throws Exception
  {
    AtomicInteger badThings = new AtomicInteger();

    try
    {
      visitor.visit(new PostgresCreateMessage("asdfasdf"), new VisitorContext("asdfasdf"))
          .thenCompose((result) ->
          {
            badThings.incrementAndGet();
            return Pnky.immediatelyComplete(null);
          }).get();
    }
    catch (Exception ex)
    {
      assertEquals(NullPointerException.class, ex.getCause().getClass());
    }

    assertEquals(badThings.get(), 0);
  }

  @Test(timeout = 100)
  public void PostgresCreateMessage_FailToCreateDatabase_FailsCallback() throws Exception
  {
    AtomicInteger badThings = new AtomicInteger();

    // make the resource fail on init;

    doThrow(new ResourceInitializationException()).when(resource).init();
    try
    {
      visitor.visit(new PostgresCreateMessage("asdfasdf"), new VisitorContext(TEST_ID))
          .thenCompose((result) ->
          {
            badThings.incrementAndGet();
            return Pnky.immediatelyComplete(null);
          }).get();
    }
    catch (Exception ex)
    {
      assertEquals(ResourceInitializationException.class, ex.getCause().getClass());
    }

    assertEquals(badThings.get(), 0);
  }

  @Test(timeout = 100)
  public void PostgresExecuteSql_NoDatabaseRegistered_Fails() throws Exception
  {
    AtomicInteger badThings = new AtomicInteger();

    try
    {
      visitor
          .visit(
              new PostgresExecSqlMessage("asdf", ResourceId.valueOf("asdfa"), "create table foo()"),
              new VisitorContext(TEST_ID)).thenCompose((result) ->
          {
            badThings.incrementAndGet();
            return Pnky.immediatelyComplete(null);
          }).get();
    }
    catch (Exception ex)
    {
      assertEquals(NullPointerException.class, ex.getCause().getClass());
    }

    assertEquals(badThings.get(), 0);
  }

  @Test(timeout = 100)
  public void PostgresExecuteSql_DatabaseRegistered_works() throws Exception
  {
    AtomicInteger badThings = new AtomicInteger();
    AtomicInteger goodThings = new AtomicInteger();

    when(resource.getId()).thenReturn(ResourceId.valueOf("asdfas234234d"));

    ConnectionInformationReply reply = createDatabase();

    try
    {
      visitor.visit(
          new PostgresExecSqlMessage("asdf", reply.getConnections().get(0)
              .getId(), "create table foo()"),
          new VisitorContext(TEST_ID)).thenCompose((result) ->
      {
        goodThings.incrementAndGet();
        return Pnky.immediatelyComplete(null);
      }).get();
    }
    catch (Exception ex)
    {
      fail();
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

package com.jive.qa.dreidel.spinnit.postgres.mocks;

import java.util.concurrent.TimeUnit;

import com.jive.qa.dreidel.api.messages.ExceptionMessage;
import com.jive.qa.dreidel.api.messages.ReplyMessage;
import com.jive.qa.dreidel.api.messages.RequestMessage;
import com.jive.qa.dreidel.spinnit.api.DreidelConnection;

public class MockedDreidelConnection implements DreidelConnection
{

  private final ReplyMessage message;
  private final ExceptionMessage exception;

  public MockedDreidelConnection(ReplyMessage message)
  {
    this.message = message;
    this.exception = null;
  }

  public MockedDreidelConnection(ExceptionMessage exception)
  {
    this.message = null;
    this.exception = exception;
  }

  @Override
  public ReplyMessage writeRequest(RequestMessage message, long timeout, TimeUnit timeUnit)
  {
    if (message != null)
    {
      return this.message;
    }
    else
    {
      return exception;
    }
  }

}

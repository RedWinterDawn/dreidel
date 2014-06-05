package com.jive.qa.dreidel.spinnit.postgres.mocks;

import java.util.concurrent.TimeUnit;

import com.jive.myco.commons.callbacks.Callback;
import com.jive.qa.dreidel.api.messages.Message;
import com.jive.qa.dreidel.spinnit.postgres.DreidelConnection;

public class MockedDreidelConnection implements DreidelConnection
{

  private final Message message;
  private final Exception exception;

  public MockedDreidelConnection(Message message)
  {
    this.message = message;
    this.exception = null;
  }

  public MockedDreidelConnection(Exception exception)
  {
    this.message = null;
    this.exception = exception;
  }

  @Override
  public void writeRequest(Message message, long timeout, TimeUnit timeUnit,
      Callback<Message> callback)
  {
    if (message != null)
    {
      callback.onSuccess(this.message);
    }
    else
    {
      callback.onFailure(exception);
    }
  }

}

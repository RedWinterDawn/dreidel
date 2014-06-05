package com.jive.qa.dreidel.spinnit.postgres.transport;

import com.google.inject.Inject;
import com.jive.myco.commons.callbacks.Callback;
import com.jive.myco.jivewire.api.exceptions.TransportInitializationException;
import com.jive.myco.jivewire.api.highlevel.HighLevelTransportConnection;
import com.jive.myco.jivewire.api.highlevel.HighLevelTransportConnectionListener;
import com.jive.myco.jivewire.api.highlevel.HighLevelTransportListener;
import com.jive.qa.dreidel.api.messages.Message;

public class PostgresTransportListener implements HighLevelTransportListener<Message, Message>
{

  @Inject
  public PostgresTransportListener(HighLevelTransportConnectionListener<Message, Message> listener,
      Callback<HighLevelTransportConnection<Message, Message>> callback)
  {
    super();
    this.listener = listener;
    this.callback = callback;
  }

  private final HighLevelTransportConnectionListener<Message, Message> listener;
  private final Callback<HighLevelTransportConnection<Message, Message>> callback;

  private boolean connected = false;

  @Override
  public void onConnected(HighLevelTransportConnection<Message, Message> connection)
  {
    connection.setListener(listener);
    if (!connected)
    {
      connected = true;
      callback.onSuccess(connection);
    }
  }

  @Override
  public void onDisconnected(HighLevelTransportConnection<Message, Message> connection)
  {
    if (!connected)
    {
      callback.onFailure(new TransportInitializationException("Never connected to the server"));
    }
  }
}

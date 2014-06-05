package com.jive.qa.dreidel.spinnit.postgres.transport;

import com.jive.myco.jivewire.api.highlevel.HighLevelTransportConnection;
import com.jive.myco.jivewire.api.highlevel.HighLevelTransportConnectionListener;
import com.jive.qa.dreidel.api.messages.Message;

public class PostgresTransportConnectionListener implements
    HighLevelTransportConnectionListener<Message, Message>
{

  @Override
  public void handleEvent(IncomingEventContext<Message, Message> context)
  {
  }

  @Override
  public void handleRequest(IncomingRequestContext<Message, Message> context)
  {
  }

  @Override
  public void onClosed(HighLevelTransportConnection<Message, Message> connection)
  {
  }

}

package com.jive.qa.dreidel.spinnit.api.transport;

import lombok.extern.slf4j.Slf4j;

import com.jive.myco.jivewire.api.highlevel.HighLevelTransportConnection;
import com.jive.myco.jivewire.api.highlevel.HighLevelTransportConnectionListener;
import com.jive.qa.dreidel.api.messages.Message;

/**
 * right now we don't support receiving requests so we just log them and black hole them
 * 
 * @author jdavidson
 *
 */
@Slf4j
public class PostgresTransportConnectionListener implements
    HighLevelTransportConnectionListener<Message, Message>
{

  @Override
  public void handleEvent(IncomingEventContext<Message, Message> context)
  {
    log.error("[{}] unnexpected Event message [{}]", context.getConnection().getId(),
        context.getMessage());
  }

  @Override
  public void handleRequest(IncomingRequestContext<Message, Message> context)
  {
    log.error("[{}] Unexpected RequestMessage [{}]", context.getConnection().getId(),
        context.getMessage());
  }

  @Override
  public void onClosed(HighLevelTransportConnection<Message, Message> connection)
  {
    log.debug("[{}] Connection Closed", connection.getId());
  }

}

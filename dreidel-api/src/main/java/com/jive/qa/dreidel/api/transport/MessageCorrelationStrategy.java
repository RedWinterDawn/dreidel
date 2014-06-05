package com.jive.qa.dreidel.api.transport;

import com.jive.myco.jivewire.api.highlevel.HighLevelTransportCorrelationStrategy;
import com.jive.qa.dreidel.api.messages.ReplyMessage;
import com.jive.qa.dreidel.api.messages.RequestMessage;
import com.jive.qa.dreidel.api.messages.TwoWayMessage;

public class MessageCorrelationStrategy implements HighLevelTransportCorrelationStrategy
{
  @Override
  public Object extractCorrelationKey(final Object message)
  {
    if (message instanceof TwoWayMessage)
    {
      return ((TwoWayMessage) message).getReferenceId();
    }
    else
    {
      return null;
    }
  }

  @Override
  public HighLevelTransportMessageType extractMessageType(final Object message)
  {
    if (message instanceof RequestMessage)
    {
      return HighLevelTransportMessageType.REQUEST;
    }
    else if (message instanceof ReplyMessage)
    {
      return HighLevelTransportMessageType.REPLY;
    }
    else
    {
      return HighLevelTransportMessageType.EVENT;
    }
  }
}

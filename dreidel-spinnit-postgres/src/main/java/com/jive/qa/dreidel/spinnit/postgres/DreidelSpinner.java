package com.jive.qa.dreidel.spinnit.postgres;

import java.util.concurrent.TimeUnit;

import lombok.AllArgsConstructor;

import com.google.common.net.HostAndPort;
import com.jive.myco.commons.callbacks.CallbackFuture;
import com.jive.myco.jivewire.api.highlevel.HighLevelTransport;
import com.jive.myco.jivewire.api.highlevel.HighLevelTransportConnection;
import com.jive.myco.jivewire.api.highlevel.HighLevelTransportListener;
import com.jive.myco.jivewire.transport.jetty.ws.JettyWebsocketHighLevelTransportFactory;
import com.jive.qa.dreidel.api.messages.ExceptionMessage;
import com.jive.qa.dreidel.api.messages.Message;
import com.jive.qa.dreidel.api.messages.ReplyMessage;
import com.jive.qa.dreidel.api.messages.RequestMessage;
import com.jive.qa.dreidel.api.transport.DreidelObjectMapper;
import com.jive.qa.dreidel.api.transport.DreidelTransportCodec;
import com.jive.qa.dreidel.api.transport.MessageCorrelationStrategy;
import com.jive.qa.dreidel.spinnit.postgres.transport.PostgresTransportConnectionListener;
import com.jive.qa.dreidel.spinnit.postgres.transport.PostgresTransportListener;

/**
 * used to gain a connection to the dreidel service
 * 
 * @author jdavidson
 *
 */
public class DreidelSpinner
{

  protected final String id;
  protected final HostAndPort hap;

  public DreidelSpinner(String id, HostAndPort hap)
  {
    this.id = id;
    this.hap = hap;
  }

  /**
   * @return a DreidelConnection
   * @throws DreidelConnectionException
   *           if there is a problem connecting to the dreidel service
   */
  public DreidelConnection connect() throws DreidelConnectionException
  {
    try
    {

      HighLevelTransport<Message, Message> transport =
          new JettyWebsocketHighLevelTransportFactory(new MessageCorrelationStrategy())
              .createClientTransport(id, "jivewire-ws://" + hap.toString()
                  + "/?encoding=text", new DreidelTransportCodec(new DreidelObjectMapper()));

      CallbackFuture<HighLevelTransportConnection<Message, Message>> callback =
          new CallbackFuture<>();
      HighLevelTransportListener<Message, Message> listener = new PostgresTransportListener(
          new PostgresTransportConnectionListener(), callback);

      transport.setListener(listener);

      CallbackFuture<Void> callback2 = new CallbackFuture<>();
      transport.init(callback2);
      callback2.get();

      return new TransportDreidelConnection(callback.get());
    }
    catch (Exception e)
    {
      throw new DreidelConnectionException(
          "There was a problem connecting to the dreidel service at " + hap.toString(), e);
    }
  }

  /**
   * a wrapper that uses jivewires connection to send messages to dreidel
   * 
   * @author jdavidson
   *
   */
  @AllArgsConstructor
  public static class TransportDreidelConnection implements DreidelConnection
  {
    private final HighLevelTransportConnection<Message, Message> connection;

    @Override
    public ReplyMessage writeRequest(RequestMessage message, long timeout, TimeUnit timeUnit)
    {
      try
      {
        CallbackFuture<Message> callback = new CallbackFuture<>();
        connection.writeRequest(message, timeout, timeUnit, callback);
        return (ReplyMessage) callback.get();
      }
      catch (Exception ex)
      {
        return new ExceptionMessage(ex, message.getReferenceId());
      }
    }
  }

}

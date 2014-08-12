package com.jive.qa.dreidel.rabbi.service;

import java.util.concurrent.ExecutionException;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.google.inject.Inject;
import com.jive.myco.commons.callbacks.CallbackFuture;
import com.jive.myco.jivewire.api.highlevel.HighLevelTransport;
import com.jive.myco.jivewire.api.highlevel.HighLevelTransportListener;
import com.jive.qa.dreidel.api.messages.Message;

/**
 * manages starting up the transport
 * 
 * @author jdavidson
 *
 */
@AllArgsConstructor(onConstructor = @__(@Inject))
@Slf4j
public class WebsocketService
{

  private final HighLevelTransport<Message, Message> transport;
  private final HighLevelTransportListener<Message, Message> listener;

  public void start() throws InterruptedException, ExecutionException
  {
    final CallbackFuture<Void> callback = new CallbackFuture<Void>();
    transport.setListener(listener);
    transport.init(callback);
    callback.get();
    log.info("Websocket server started");
  }

  public void stop() throws InterruptedException, ExecutionException
  {
    log.info("Destroying websocket server");
    final CallbackFuture<Void> callback = new CallbackFuture<Void>();
    transport.destroy(callback);
    callback.get();

  }
}

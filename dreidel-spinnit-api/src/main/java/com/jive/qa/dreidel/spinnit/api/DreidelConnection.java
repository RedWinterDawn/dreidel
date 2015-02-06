package com.jive.qa.dreidel.spinnit.api;

import java.io.Closeable;
import java.util.concurrent.TimeUnit;

import com.jive.qa.dreidel.api.messages.ReplyMessage;
import com.jive.qa.dreidel.api.messages.RequestMessage;

/**
 * exposes the ability to send requests to the dreidel server
 *
 * @author jdavidson
 *
 */
public interface DreidelConnection extends Closeable
{
  ReplyMessage writeRequest(RequestMessage message, long timeout, TimeUnit timeUnit);

  void close() throws DreidelConnectionException;
}

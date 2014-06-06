package com.jive.qa.dreidel.spinnit.postgres;

import java.util.concurrent.TimeUnit;

import com.jive.myco.commons.callbacks.Callback;
import com.jive.qa.dreidel.api.messages.Message;

/**
 * exposes the ability to send requests to the dreidel server
 * 
 * @author jdavidson
 *
 */
public interface DreidelConnection
{
  void writeRequest(Message message, long timeout, TimeUnit timeUnit, Callback<Message> callback);
}

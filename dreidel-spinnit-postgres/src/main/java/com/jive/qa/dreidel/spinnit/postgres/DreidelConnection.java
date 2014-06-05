package com.jive.qa.dreidel.spinnit.postgres;

import java.util.concurrent.TimeUnit;

import com.jive.myco.commons.callbacks.Callback;
import com.jive.qa.dreidel.api.messages.Message;

public interface DreidelConnection
{

  void writeRequest(Message message, long timeout, TimeUnit timeUnit, Callback<Message> callback);

}

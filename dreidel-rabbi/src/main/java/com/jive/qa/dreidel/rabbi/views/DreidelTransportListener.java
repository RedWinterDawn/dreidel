package com.jive.qa.dreidel.rabbi.views;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.jive.myco.jivewire.api.highlevel.HighLevelTransportConnection;
import com.jive.myco.jivewire.api.highlevel.HighLevelTransportConnectionListener;
import com.jive.myco.jivewire.api.highlevel.HighLevelTransportListener;
import com.jive.qa.dreidel.api.exceptions.ResourceDestructionException;
import com.jive.qa.dreidel.api.messages.Message;
import com.jive.qa.dreidel.rabbi.resources.BaseResource;

@AllArgsConstructor(onConstructor = @__(@Inject))
@Slf4j
public class DreidelTransportListener implements HighLevelTransportListener<Message, Message>
{
  HighLevelTransportConnectionListener<Message, Message> listener;
  Map<String, List<BaseResource>> resourceCorrelationMap;

  @Override
  public void onConnected(final HighLevelTransportConnection<Message, Message> connection)
  {
    connection.setListener(listener);
    resourceCorrelationMap.put(connection.getId(), Lists.newArrayList());
  }

  @Override
  public void onDisconnected(final HighLevelTransportConnection<Message, Message> connection)
  {
    for (BaseResource model : resourceCorrelationMap.get(connection.getId()))
    {
      try
      {
        model.destroy();
      }
      catch (ResourceDestructionException e)
      {
        log.error("There was a problem destroying the resource {} {}", model.getId(), model
            .getClass().getSimpleName(), e);
      }
    }
  }
}

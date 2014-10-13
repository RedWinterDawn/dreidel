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

/**
 * TransportListener that wires up a connection listener onConnected
 *
 * @author jdavidson
 *
 */
@AllArgsConstructor(onConstructor = @__(@Inject))
@Slf4j
public final class DreidelTransportListener implements HighLevelTransportListener<Message, Message>
{
  private final HighLevelTransportConnectionListener<Message, Message> listener;
  private final Map<String, List<BaseResource>> resourceCorrelationMap;
  private final List<BaseResource> resourcesToDestroy = Lists.newCopyOnWriteArrayList();

  @Override
  public void onConnected(final HighLevelTransportConnection<Message, Message> connection)
  {
    connection.setListener(listener);
    resourceCorrelationMap.put(connection.getId(), Lists.newArrayList());
  }

  @Override
  public void onDisconnected(final HighLevelTransportConnection<Message, Message> connection)
  {

    for (BaseResource model : resourcesToDestroy)
    {
      try
      {
        model.destroy();
        resourcesToDestroy.remove(model);
        log.info("destroyed resource {}", model);
      }
      catch (ResourceDestructionException e)
      {
        if (e.getCause().getMessage().contains("does not exist"))
        {
          resourcesToDestroy.remove(model);
        }
        log.error("There was a problem destroying the resource {} {}", model.getId(), model
            .getClass().getSimpleName(), e);
      }
    }

    List<BaseResource> resources = resourceCorrelationMap.get(connection.getId());

    resourcesToDestroy.addAll(resources);
  }
}

package com.jive.qa.dreidel.rabbi.visitors;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import lombok.AllArgsConstructor;

import com.google.common.collect.Lists;
import com.jive.myco.commons.concurrent.Pnky;
import com.jive.myco.commons.concurrent.PnkyPromise;
import com.jive.qa.dreidel.api.interfaces.JinstVisitor;
import com.jive.qa.dreidel.api.messages.VisitorContext;
import com.jive.qa.dreidel.api.messages.jinst.JinstCreateMessage;
import com.jive.qa.dreidel.api.replies.ConnectionInformation;
import com.jive.qa.dreidel.api.replies.ConnectionInformationReply;
import com.jive.qa.dreidel.api.replies.Reply;
import com.jive.qa.dreidel.rabbi.resources.BaseResource;
import com.jive.qa.dreidel.rabbi.resources.JinstResource;
import com.jive.qa.dreidel.rabbi.resources.ResourceFactory;

@AllArgsConstructor
public class JinstVisitorImpl implements JinstVisitor<Reply, VisitorContext>
{

  private final ExecutorService executor;
  private final Map<String, List<BaseResource>> resourceCorrelationMap;
  private final ResourceFactory resourceFactory;

  @Override
  public PnkyPromise<Reply> visit(JinstCreateMessage message, VisitorContext context)
  {
    Pnky<Reply> promise = Pnky.create();

    JinstResource resource =
        resourceFactory.getJinstResource(message.getJClass(), message.getBranch());

    List<BaseResource> resources = resourceCorrelationMap.get(context.getId());

    resources.add(resource);

    executor.execute(new JinstRunner(promise, resource));

    return promise;
  }

  @AllArgsConstructor
  private class JinstRunner implements Runnable
  {

    private final Pnky<Reply> promise;
    private final JinstResource resource;

    @Override
    public void run()
    {
      try
      {
        // TODO concurrency checks
        resource.init();

        List<ConnectionInformation> connections = Lists.newArrayList();

        connections.add(new ConnectionInformation("jinst", resource.getId(), resource.getHap()
            .getHostText(), resource.getHap()
            .getPort(), null));

        ConnectionInformationReply reply = new ConnectionInformationReply(connections);

        promise.resolve(reply);
      }
      catch (Exception e)
      {
        promise.reject(e);
      }
    }
  }

}

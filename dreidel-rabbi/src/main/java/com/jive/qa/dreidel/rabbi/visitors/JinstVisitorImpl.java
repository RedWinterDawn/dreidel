package com.jive.qa.dreidel.rabbi.visitors;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import lombok.AllArgsConstructor;

import com.google.common.collect.Lists;
import com.jive.myco.commons.concurrent.Pnky;
import com.jive.myco.commons.concurrent.PnkyPromise;
import com.jive.qa.dreidel.api.interfaces.JinstVisitor;
import com.jive.qa.dreidel.api.messages.ResourceId;
import com.jive.qa.dreidel.api.messages.VisitorContext;
import com.jive.qa.dreidel.api.messages.goyim.IdResponse;
import com.jive.qa.dreidel.api.messages.jinst.JinstCreateMessage;
import com.jive.qa.dreidel.api.replies.ConnectionInformation;
import com.jive.qa.dreidel.api.replies.ConnectionInformationReply;
import com.jive.qa.dreidel.api.replies.Reply;
import com.jive.qa.restinator.Endpoint;

public class JinstVisitorImpl implements JinstVisitor<Reply, VisitorContext>
{

  private final Endpoint<IdResponse, Void> instanceCreator;
  private final ExecutorService executor;

  public JinstVisitorImpl(Endpoint<IdResponse, Void> instanceCreator)
  {
    this.instanceCreator = instanceCreator;
    this.executor = Executors.newFixedThreadPool(10);
  }

  @Override
  public PnkyPromise<Reply> visit(JinstCreateMessage message, VisitorContext context)
  {

    Pnky<Reply> promise = Pnky.create();

    executor.execute(new JinstRunner(promise, message));

    return promise;
  }

  @AllArgsConstructor
  private class JinstRunner implements Runnable
  {

    private final Pnky<Reply> promise;
    private final JinstCreateMessage message;

    @Override
    public void run()
    {
      try
      {
        IdResponse response = instanceCreator.url("/" + message.getJClass()).post();

        List<ConnectionInformation> connections = Lists.newArrayList();

        connections.add(new ConnectionInformation("jinst", ResourceId.valueOf(Integer
            .toString(response.getId())), response.getAddress(), -1, null));

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

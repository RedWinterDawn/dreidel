package com.jive.qa.dreidel.rabbi.visitors;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import lombok.AllArgsConstructor;

import com.google.common.collect.Lists;
import com.google.common.net.HostAndPort;
import com.jive.myco.commons.concurrent.Pnky;
import com.jive.myco.commons.concurrent.PnkyPromise;
import com.jive.myco.commons.io.AvailablePortFinder;
import com.jive.qa.dreidel.api.interfaces.WiremockVisitor;
import com.jive.qa.dreidel.api.messages.VisitorContext;
import com.jive.qa.dreidel.api.messages.wiremock.WiremockCreateMessage;
import com.jive.qa.dreidel.api.replies.ConnectionInformation;
import com.jive.qa.dreidel.api.replies.ConnectionInformationReply;
import com.jive.qa.dreidel.api.replies.Reply;
import com.jive.qa.dreidel.rabbi.resources.BaseResource;
import com.jive.qa.dreidel.rabbi.resources.ResourceFactory;
import com.jive.qa.dreidel.rabbi.resources.WiremockResource;
import com.jive.qa.dreidel.rabbi.service.WireMockConfiguration;

@AllArgsConstructor
public class WiremockVisitorImpl implements WiremockVisitor<Reply, VisitorContext>
{

  private final ExecutorService executor;
  private final Map<String, List<BaseResource>> resourceCorrelationMap;
  private final ResourceFactory resourceFactory;
  private final WireMockConfiguration configuration;

  @Override
  public PnkyPromise<Reply> visit(WiremockCreateMessage message, VisitorContext context)
  {
    Pnky<Reply> promise = Pnky.create();

    WiremockResource resource =
        resourceFactory.getWiremockResource(HostAndPort.fromParts(configuration.getHost(),
            AvailablePortFinder.getNextAvailable()));

    List<BaseResource> resources = resourceCorrelationMap.get(context.getId());

    resources.add(resource);

    executor.execute(new WiremockRunner(promise, resource));

    return promise;
  }

  @AllArgsConstructor
  private class WiremockRunner implements Runnable
  {

    private final Pnky<Reply> promise;
    private final WiremockResource resource;

    @Override
    public void run()
    {
      try
      {
        // TODO concurrency checks
        resource.init();

        List<ConnectionInformation> connections = Lists.newArrayList();

        connections.add(new ConnectionInformation("wiremock", resource.getId(), resource.getHap()
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

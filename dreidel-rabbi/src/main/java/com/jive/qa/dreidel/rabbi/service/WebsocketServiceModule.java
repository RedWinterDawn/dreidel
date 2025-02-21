package com.jive.qa.dreidel.rabbi.service;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

import lombok.extern.slf4j.Slf4j;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Named;
import com.jive.myco.jivewire.api.codec.TransportCodec;
import com.jive.myco.jivewire.api.exceptions.TransportUriFormatException;
import com.jive.myco.jivewire.api.highlevel.HighLevelTransport;
import com.jive.myco.jivewire.api.highlevel.HighLevelTransportConnectionListener;
import com.jive.myco.jivewire.api.highlevel.HighLevelTransportCorrelationStrategy;
import com.jive.myco.jivewire.api.highlevel.HighLevelTransportFactory;
import com.jive.myco.jivewire.api.highlevel.HighLevelTransportListener;
import com.jive.myco.jivewire.transport.jetty.ws.JettyWebsocketHighLevelTransportFactory;
import com.jive.qa.dreidel.api.interfaces.JinstVisitor;
import com.jive.qa.dreidel.api.interfaces.MessageCategoryVisitor;
import com.jive.qa.dreidel.api.interfaces.PostgresVisitor;
import com.jive.qa.dreidel.api.interfaces.WiremockVisitor;
import com.jive.qa.dreidel.api.messages.Message;
import com.jive.qa.dreidel.api.messages.VisitorContext;
import com.jive.qa.dreidel.api.messages.goyim.IdResponse;
import com.jive.qa.dreidel.api.messages.goyim.ResponseCodeOnly;
import com.jive.qa.dreidel.api.replies.Reply;
import com.jive.qa.dreidel.api.transport.DreidelObjectMapper;
import com.jive.qa.dreidel.api.transport.DreidelTransportCodec;
import com.jive.qa.dreidel.api.transport.MessageCorrelationStrategy;
import com.jive.qa.dreidel.goyim.GoyimCreationCodec;
import com.jive.qa.dreidel.goyim.GoyimDeletionCodec;
import com.jive.qa.dreidel.rabbi.resources.BaseResource;
import com.jive.qa.dreidel.rabbi.resources.ResourceFactory;
import com.jive.qa.dreidel.rabbi.resources.ResourceFactoryImpl;
import com.jive.qa.dreidel.rabbi.views.DreidelTransportConnectionListener;
import com.jive.qa.dreidel.rabbi.views.DreidelTransportListener;
import com.jive.qa.dreidel.rabbi.visitors.JinstVisitorImpl;
import com.jive.qa.dreidel.rabbi.visitors.PostgresVisitorImpl;
import com.jive.qa.dreidel.rabbi.visitors.RoutingVisitor;
import com.jive.qa.dreidel.rabbi.visitors.WiremockVisitorImpl;
import com.jive.qa.restinator.Endpoint;
import com.jive.qa.restinator.codecs.ByteArrayEndpointCodec;

@Slf4j
public class WebsocketServiceModule extends AbstractModule
{

  private final String id = "rabbi websocket server";

  @Override
  protected void configure()
  {
    bind(WebsocketService.class).asEagerSingleton();

    bind(TransportCodec.class).to(DreidelTransportCodec.class).asEagerSingleton();

    bind(HighLevelTransportCorrelationStrategy.class).to(MessageCorrelationStrategy.class)
        .asEagerSingleton();

    bind(new TypeLiteral<HighLevelTransportListener<Message, Message>>()
    {
    }).to(DreidelTransportListener.class).asEagerSingleton();

    bind(new TypeLiteral<HighLevelTransportConnectionListener<Message, Message>>()
    {
    }).to(DreidelTransportConnectionListener.class).asEagerSingleton();

    bind(new TypeLiteral<MessageCategoryVisitor<Reply, VisitorContext>>()
    {
    }).to(RoutingVisitor.class).asEagerSingleton();

    bind(new TypeLiteral<PostgresVisitor<Reply, VisitorContext>>()
    {
    }).to(PostgresVisitorImpl.class).asEagerSingleton();

    bind(new TypeLiteral<Map<String, List<BaseResource>>>()
    {
    }).toInstance(Maps.newConcurrentMap());

    bind(new TypeLiteral<TransportCodec<Message, Message>>()
    {
    }).to(DreidelTransportCodec.class);

    bind(ResourceFactory.class).to(ResourceFactoryImpl.class).asEagerSingleton();

    bind(ObjectMapper.class).to(DreidelObjectMapper.class).asEagerSingleton();

  }

  @Provides
  public HighLevelTransport<Message, Message> getTransport(final HighLevelTransportFactory factory,
      @Named("serverUri") final String uri, final TransportCodec<Message, Message> codec)
      throws TransportUriFormatException
  {
    log.info("binding to {}", uri);
    return factory.createServerTransport(id, uri, codec);
  }

  @Provides
  public HighLevelTransportFactory getTransportFactory(
      final HighLevelTransportCorrelationStrategy correlationStrategy)
  {
    return new JettyWebsocketHighLevelTransportFactory(correlationStrategy);
  }

  @Provides
  @Named("creationEndpoint")
  public Endpoint<IdResponse, Void> getCreationEndpoint(
      @Named("creationCodec") ByteArrayEndpointCodec<IdResponse, Void> codec)
  {
    return new Endpoint<IdResponse, Void>(codec);
  }

  @Provides
  @Named("deletionEndpoint")
  public Endpoint<ResponseCodeOnly, Void> getDestructionEndpoint(
      @Named("deletionCodec") ByteArrayEndpointCodec<ResponseCodeOnly, Void> codec)
  {
    return new Endpoint<ResponseCodeOnly, Void>(codec);
  }

  @Provides
  @Named("creationCodec")
  ByteArrayEndpointCodec<IdResponse, Void> getCreationCodec(ObjectMapper json)
  {
    return new GoyimCreationCodec(json);
  }

  @Provides
  @Named("deletionCodec")
  ByteArrayEndpointCodec<ResponseCodeOnly, Void> getDeletionCodec()
  {
    return new GoyimDeletionCodec();
  }

  @Provides
  @Named("creationUrl")
  URL getCreationUrl(@Named("goyimIp") String ip, @Named("goyimPort") int port)
      throws MalformedURLException
  {
    return new URL("http://" + ip + ":" + port);
  }

  @Provides
  JinstVisitor<Reply, VisitorContext> getJistVisitorImpl(
      Map<String, List<BaseResource>> resourceCorrelationMap, ResourceFactory factory)
  {
    return new JinstVisitorImpl(Executors.newFixedThreadPool(10), resourceCorrelationMap, factory);
  }

  @Provides
  WiremockVisitor<Reply, VisitorContext> getWireMockVisitorImpl(
      Map<String, List<BaseResource>> resourceCorrelationMap, ResourceFactory factory,
      WireMockConfiguration configuration)
  {
    return new WiremockVisitorImpl(Executors.newFixedThreadPool(10), resourceCorrelationMap,
        factory, configuration);
  }
}

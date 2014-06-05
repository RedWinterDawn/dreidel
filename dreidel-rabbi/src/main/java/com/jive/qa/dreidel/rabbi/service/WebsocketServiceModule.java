package com.jive.qa.dreidel.rabbi.service;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Named;
import com.jive.jackson.ConstructorPropertiesAnnotationIntrospector;
import com.jive.myco.jivewire.api.codec.TransportCodec;
import com.jive.myco.jivewire.api.exceptions.TransportUriFormatException;
import com.jive.myco.jivewire.api.highlevel.HighLevelTransport;
import com.jive.myco.jivewire.api.highlevel.HighLevelTransportConnectionListener;
import com.jive.myco.jivewire.api.highlevel.HighLevelTransportCorrelationStrategy;
import com.jive.myco.jivewire.api.highlevel.HighLevelTransportFactory;
import com.jive.myco.jivewire.api.highlevel.HighLevelTransportListener;
import com.jive.myco.jivewire.transport.jetty.ws.JettyWebsocketHighLevelTransportFactory;
import com.jive.qa.dreidel.api.interfaces.MessageCategoryVisitor;
import com.jive.qa.dreidel.api.interfaces.PostgresVisitor;
import com.jive.qa.dreidel.api.messages.Message;
import com.jive.qa.dreidel.api.messages.VisitorContext;
import com.jive.qa.dreidel.api.replies.Reply;
import com.jive.qa.dreidel.api.transport.DreidelTransportCodec;
import com.jive.qa.dreidel.api.transport.MessageCorrelationStrategy;
import com.jive.qa.dreidel.rabbi.resources.BaseResource;
import com.jive.qa.dreidel.rabbi.resources.ResourceFactory;
import com.jive.qa.dreidel.rabbi.resources.ResourceFactoryImpl;
import com.jive.qa.dreidel.rabbi.views.DreidelTransportConnectionListener;
import com.jive.qa.dreidel.rabbi.views.DreidelTransportListener;
import com.jive.qa.dreidel.rabbi.views.PostgresVisitorImpl;
import com.jive.qa.dreidel.rabbi.views.RoutingVisitor;

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
    }).toInstance(Maps.newHashMap());

    bind(new TypeLiteral<TransportCodec<Message, Message>>()
    {
    }).to(DreidelTransportCodec.class);

    bind(ResourceFactory.class).to(ResourceFactoryImpl.class).asEagerSingleton();

  }

  @Provides
  public HighLevelTransport<Message, Message> getTransport(final HighLevelTransportFactory factory,
      @Named("serverUri") final String uri, final TransportCodec<Message, Message> codec)
      throws TransportUriFormatException
  {
    return factory.createServerTransport(id, uri, codec);
  }

  @Provides
  public HighLevelTransportFactory getTransportFactory(
      final HighLevelTransportCorrelationStrategy correlationStrategy)
  {
    return new JettyWebsocketHighLevelTransportFactory(correlationStrategy);
  }

  @Provides
  public ObjectMapper getJson()
  {
    ObjectMapper json = new ObjectMapper();
    ConstructorPropertiesAnnotationIntrospector.install(json);
    return json;
  }
}

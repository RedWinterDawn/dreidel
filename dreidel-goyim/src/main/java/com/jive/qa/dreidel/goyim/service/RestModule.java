package com.jive.qa.dreidel.goyim.service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.nio.client.HttpAsyncClient;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.jive.myco.commons.callbacks.CallbackFuture;
import com.jive.qa.dreidel.goyim.controllers.JimController;
import com.jive.qa.dreidel.goyim.messages.JimMessage;
import com.jive.qa.dreidel.goyim.models.InstanceDetails;
import com.jive.qa.dreidel.goyim.rest.JimResource;
import com.jive.qa.dreidel.goyim.restinator.DreidelObjectMapper;
import com.jive.qa.dreidel.goyim.restinator.JimCodec;
import com.jive.qa.dreidel.goyim.views.DreidelView;
import com.jive.qa.restinator.Endpoint;
import com.jive.qa.restinator.codecs.ByteArrayEndpointCodec;
import com.jive.v5.commons.rest.client.RestClient;

public class RestModule extends AbstractModule
{

  @Provides
  @Singleton
  @Named("rest-server-resources")
  public Set<Class<?>> getJaxRsResources()
  {
    final Set<Class<?>> resources = Sets.newHashSet();
    resources.add(DreidelView.class);
    return resources;
  }

  @Override
  protected void configure()
  {
    bind(ByteArrayEndpointCodec.class).to(JimCodec.class).asEagerSingleton();
    bind(ObjectMapper.class).to(DreidelObjectMapper.class).asEagerSingleton();
    bind(JimSettings.class).asEagerSingleton();
    bind(BmSettings.class).asEagerSingleton();
    bind(JimController.class).asEagerSingleton();
  }

  @Provides
  @Named("rest-server-immediate")
  public boolean getImmediate(@Named("rest-server-immediate") final String immediate)
  {
    return Boolean.valueOf(immediate);
  }

  @Provides
  @Named("networksMap")
  public Map<String, String> getNetworkMap(@Named("bm.networks") final String networks,
      final ObjectMapper json)
      throws JsonParseException, JsonMappingException, IOException
  {
    return json.readValue(networks, new TypeReference<HashMap<String, String>>()
    {
    });
  }

  @Provides
  @Named("jimEndpoint")
  @Singleton
  public Endpoint<JimMessage, JimMessage> getEndpoint(final JimCodec codec,
      @Named("jim.key") final String key)
  {
    final Map<String, String> headers = Maps.newHashMap();
    headers.put("Authorization", "Token  token=" + key);
    final Endpoint<JimMessage, JimMessage> rtn =
        new Endpoint<JimMessage, JimMessage>(codec, headers);
    return rtn;
  }

  @Provides
  @Named("jimUrl")
  @Singleton
  public URL getJimUrl(final JimSettings settings) throws MalformedURLException
  {
    return new URL("http://" + settings.getIp() + ":" + settings.getPort() + "/" + settings.getEp());
  }

  @Provides
  @Named("serverCorrelationMap")
  @Singleton
  public Map<String, CallbackFuture<Void>> getServerCorrelationMap()
  {
    return Maps.newConcurrentMap();
  }

  @Provides
  public HttpAsyncClient getClient()
  {
    final CloseableHttpAsyncClient client = HttpAsyncClients.createMinimal();
    client.start();
    return client;
  }

  @Provides
  public JimResource getJimResource(final HttpAsyncClient client, final ObjectMapper mapper,
      @Named("jimUrl") final URL url)
  {
    final RestClient restClient = new RestClient(client, mapper);
    return restClient.bind(url.toString(), JimResource.class);
  }

  @Provides
  @Singleton
  public Map<String, InstanceDetails> getInstanceDetailsMap()
  {
    return Maps.newConcurrentMap();
  }

}

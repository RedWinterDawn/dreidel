package com.jive.qa.dreidel.goyim.service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.http.client.config.RequestConfig;
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
import com.jive.jackson.ConstructorPropertiesAnnotationIntrospector;
import com.jive.myco.commons.callbacks.CallbackFuture;
import com.jive.qa.dreidel.goyim.controllers.JimController;
import com.jive.qa.dreidel.goyim.models.InstanceDetails;
import com.jive.qa.dreidel.goyim.rest.JimResource;
import com.jive.qa.dreidel.goyim.rest.JimService;
import com.jive.qa.dreidel.goyim.views.DreidelView;
import com.jive.v5.commons.rest.client.RestClient;

public class RestModule extends AbstractModule
{

  @Override
  protected void configure()
  {
    bind(JimSettings.class).asEagerSingleton();
    bind(BmSettings.class).asEagerSingleton();
    bind(JimController.class).asEagerSingleton();
  }

  @Provides
  @Singleton
  @Named("rest-server-resources")
  public Set<Class<?>> getJaxRsResources()
  {
    final Set<Class<?>> resources = Sets.newHashSet();
    resources.add(DreidelView.class);
    return resources;
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
  @Named("jimUrl")
  @Singleton
  public URL getJimUrl(final JimSettings settings) throws MalformedURLException
  {
    return new URL("http://" + settings.getIp() + ":" + settings.getPort());
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
    RequestConfig config = RequestConfig.custom()
        .setConnectTimeout(1000)
        .setSocketTimeout(20000)
        .setMaxRedirects(2)
        .setRedirectsEnabled(true)
        .setRelativeRedirectsAllowed(true)
        .build();
    CloseableHttpAsyncClient client =
        HttpAsyncClients.custom().setDefaultRequestConfig(config).build();
    client.start();
    return client;
  }

  @Provides
  public ObjectMapper getMapper()
  {
    ObjectMapper mapper = new ObjectMapper();
    ConstructorPropertiesAnnotationIntrospector.install(mapper);

    return mapper;
  }

  @Provides
  public JimResource getJimResource(@Named("jimUrl") final URL url, ObjectMapper mapper,
      HttpAsyncClient client)
  {
    RestClient restClient = new RestClient(client, mapper);
    return restClient.bind(url.toString(), JimResource.class);
  }

  @Provides
  @Singleton
  public Map<String, InstanceDetails> getInstanceDetailsMap()
  {
    return Maps.newConcurrentMap();
  }

  @Provides
  public JimService getJimService(JimResource endpoint, @Named("jim.key") String token)
  {
    return new JimService(endpoint, token);
  }
}

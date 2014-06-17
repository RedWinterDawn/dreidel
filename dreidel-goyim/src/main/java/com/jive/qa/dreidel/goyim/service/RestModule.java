package com.jive.qa.dreidel.goyim.service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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
import com.jive.qa.dreidel.goyim.controllers.InstanceManager;
import com.jive.qa.dreidel.goyim.controllers.JimController;
import com.jive.qa.dreidel.goyim.messages.JimMessage;
import com.jive.qa.dreidel.goyim.restinator.DreidelObjectMapper;
import com.jive.qa.dreidel.goyim.restinator.JimCodec;
import com.jive.qa.dreidel.goyim.views.DreidelView;
import com.jive.qa.restinator.Endpoint;
import com.jive.qa.restinator.codecs.ByteArrayEndpointCodec;

public class RestModule extends AbstractModule
{

  @Provides
  @Singleton
  @Named("rest-server-resources")
  public Set<Class<?>> getJaxRsResources()
  {
    Set<Class<?>> resources = Sets.newHashSet();
    resources.add(DreidelView.class);
    return resources;
  }

  @Override
  protected void configure()
  {
    bind(ByteArrayEndpointCodec.class).to(JimCodec.class).asEagerSingleton();
    bind(ObjectMapper.class).to(DreidelObjectMapper.class).asEagerSingleton();
    bind(InstanceManager.class).asEagerSingleton();
    bind(JimSettings.class).asEagerSingleton();
    bind(BmSettings.class).asEagerSingleton();
    bind(JimController.class).asEagerSingleton();
  }

  @Provides
  @Named("rest-server-immediate")
  public boolean getImmediate(@Named("rest-server-immediate") String immediate)
  {
    return Boolean.valueOf(immediate);
  }

  @Provides
  @Named("networksMap")
  public Map<String, String> getNetworkMap(@Named("bm.networks") String networks, ObjectMapper json)
      throws JsonParseException, JsonMappingException, IOException
  {
    return json.readValue(networks, new TypeReference<HashMap<String, String>>()
    {
    });
  }

  @Provides
  @Named("jimEndpoint")
  @Singleton
  public Endpoint<JimMessage, JimMessage> getEndpoint(JimCodec codec)
  {
    Map<String, String> headers = Maps.newHashMap();
    headers.put("Authorization", "Token  token=27ad89ff37787846892251008b700fdb");
    Endpoint<JimMessage, JimMessage> rtn =
        new Endpoint<JimMessage, JimMessage>(codec, headers);
    return rtn;
  }

  @Provides
  @Named("jimUrl")
  @Singleton
  public URL getJimUrl(JimSettings settings) throws MalformedURLException
  {
    return new URL("http://" + settings.getIp() + ":" + settings.getPort() + "/" + settings.getEp());
  }

}

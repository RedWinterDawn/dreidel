package com.jive.qa.dreidel.goyim.jinst.service;

import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.nio.client.HttpAsyncClient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Named;
import com.jive.jackson.ConstructorPropertiesAnnotationIntrospector;
import com.jive.qa.dreidel.goyim.jinst.service.postback.PostResults;
import com.jive.qa.dreidel.goyim.jinst.service.rest.GoyimView;

/**
 * Example of a module that boostsraps the classes used by the Jazz Service.
 *
 * @author David Valeri
 */
public class ExampleServiceModule extends AbstractModule
{
  @Override
  protected void configure()
  {
    bind(GoyimView.class).asEagerSingleton();
    // bind(RestServerManager.class).asEagerSingleton();
    bind(PostResults.class).asEagerSingleton();
  }

  @Provides
  @Named("goyimUrl")
  public String getGoyimUrl(@Named("goyimPort") int port, @Named("goyimIp") String ip)
  {
    return "http://" + ip + ":" + port;
  }

  @Provides
  public HttpAsyncClient getClient()
  {
    CloseableHttpAsyncClient client = HttpAsyncClients.createMinimal();
    client.start();
    return client;
  }

  @Provides
  public ObjectMapper getObjectMapper()
  {
    ObjectMapper json = new ObjectMapper();
    ConstructorPropertiesAnnotationIntrospector.install(json);
    return json;
  }

}

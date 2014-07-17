package com.jive.qa.dreidel.goyim.jinst.service;

import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.nio.client.HttpAsyncClient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Named;
import com.jive.jackson.ConstructorPropertiesAnnotationIntrospector;

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

    bind(PostResults.class).asEagerSingleton();
  }

  @Provides
  @Named("goyimUrl")
  public String getGoyimUrl()
  {
    return "http://10.20.27.88:8019";
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

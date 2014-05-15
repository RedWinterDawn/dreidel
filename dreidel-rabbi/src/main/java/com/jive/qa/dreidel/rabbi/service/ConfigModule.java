package com.jive.qa.dreidel.rabbi.service;

import java.io.IOException;
import java.util.Properties;

import com.google.inject.Provides;
import com.google.inject.name.Named;
import com.jive.myco.jin.config.AbstractConfigModule;

/**
 * Example of a configuration module used to bind property values into the context.
 * 
 * @author David Valeri
 */
public class ConfigModule extends AbstractConfigModule
{
  @Override
  protected void configure()
  {
    // Load the defaults from the classpath
    final Properties properties = new Properties();
    try
    {
      properties.load(getClass().getResourceAsStream("/META-INF/default.properties"));
    }
    catch (final IOException e)
    {
      throw new RuntimeException("This is bad.");
    }

    properties()
        // These are the defaults loaded above
        .fromProperties(properties)
        // This resolvable ID is put here by the container
        .fromResolvableId("jazz.service.properties")
        .named("properties");

    // Import every property value in the properties
    property()
        .fromNamedProperties("properties")
        .withAllKeys();
  }

  @Provides
  @Named("postgresPort")
  public int getPort(@Named("postgresPort") final String port)
  {
    return Integer.valueOf(port);
  }

}

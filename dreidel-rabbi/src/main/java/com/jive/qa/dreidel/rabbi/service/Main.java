package com.jive.qa.dreidel.rabbi.service;

import java.util.Properties;

import com.google.common.collect.Lists;
import com.google.inject.Module;
import com.jive.myco.jazz.injector.JazzInjectorManager;
import com.jive.myco.jin.InjectorManager;
import com.jive.myco.jin.config.MappedPropertiesSourceResolver;
import com.jive.myco.jin.config.MappedPropertiesSourceResolver.MappedPropertiesSourceResolverBuilder;

public class Main
{

  private static InjectorManager manager;

  public static void main(final String[] args)
  {
    Properties properties = new Properties();
    properties.setProperty("postgresHost", "localhost");
    properties.setProperty("postgresPort", "5432");
    properties.setProperty("postgresUsername", "postgres");
    properties.setProperty("postgresPassword", "");
    properties.setProperty("inetName", "v4compat");
    properties.setProperty("timeToExpire", "240");

    MappedPropertiesSourceResolver resolver = new MappedPropertiesSourceResolverBuilder().withPid(
        "jazz.service.properties", properties).build();
    manager =
        new JazzInjectorManager("dreidel-rabbi", Lists.<Module> newArrayList(new ConfigModule(),
            new ServiceModule()), resolver);
    manager.start();
  }

}

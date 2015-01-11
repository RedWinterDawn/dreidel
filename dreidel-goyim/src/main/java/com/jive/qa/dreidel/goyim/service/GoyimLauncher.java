package com.jive.qa.dreidel.goyim.service;

import lombok.Getter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Names;
import com.jive.myco.config.PropertiesJazzConfiguration;
import com.jive.myco.jazz.api.runtime.JazzCore;
import com.jive.myco.jazz.api.runtime.JazzRuntimeEnvironment;
import com.jive.myco.jazz.runtime.SimpleAbstractJazzRuntimeLauncher;
import com.jive.qa.dreidel.goyim.views.DreidelView;

public class GoyimLauncher extends SimpleAbstractJazzRuntimeLauncher
{

  @Getter
  private final String defaultServiceName = "dreidel-goyim";

  Injector injector;
  private RestServerManager dreidel;

  @Override
  protected void postStart(final JazzRuntimeEnvironment jazzRuntimeEnvironment,
      final JazzCore<PropertiesJazzConfiguration> jazzCore) throws Exception
  {
    injector = Guice.createInjector(new RestModule(), new AbstractModule()
    {
      @Override
      protected void configure()
      {
        Names.bindProperties(binder(), jazzRuntimeEnvironment.getProperties());
      }
    });

    dreidel = new RestServerManager(jazzRuntimeEnvironment, jazzCore.getRestServiceManager().get(),
        injector.getInstance(DreidelView.class),
        injector.getInstance(ObjectMapper.class),
        injector.getInstance(Key.get(Integer.class, Names.named("rest-server-port"))));

    dreidel.start();
  }

  @Override
  protected void preDestroy(final JazzRuntimeEnvironment jazzRuntimeEnvironment) throws Exception
  {
    if (dreidel != null)
    {
      dreidel.stop();
    }
  }

  public static void main(final String[] args)
  {
    new GoyimLauncher().launch(args);
  }
}

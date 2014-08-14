package com.jive.qa.dreidel.rabbi.service;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.name.Names;
import com.jive.myco.config.PropertiesJazzConfiguration;
import com.jive.myco.jazz.api.runtime.JazzCore;
import com.jive.myco.jazz.api.runtime.JazzRuntimeEnvironment;
import com.jive.myco.jazz.runtime.SimpleAbstractJazzRuntimeLauncher;

public class Main extends SimpleAbstractJazzRuntimeLauncher
{
  private Injector injector;
  private WebsocketService websocketService;

  public static void main(final String[] args)
  {
    final Main main = new Main();
    main.launch(args);
  }

  @Override
  public String getDefaultServiceName()
  {
    return "dreidel-rabbi";
  }

  @Override
  protected void postInit(final JazzRuntimeEnvironment jazzRuntimeEnvironment) throws Exception
  {
    injector = Guice.createInjector(
        new WebsocketServiceModule(),
        new AbstractModule()
        {
          @Override
          protected void configure()
          {
            Names.bindProperties(binder(), jazzRuntimeEnvironment.getProperties());
          }
        });
  }

  @Override
  protected void postStart(final JazzRuntimeEnvironment jazzRuntimeEnvironment,
      final JazzCore<PropertiesJazzConfiguration> jazzCore) throws Exception
  {
    websocketService = injector.getInstance(WebsocketService.class);
    websocketService.start();
  }

  @Override
  protected void preStop(final JazzRuntimeEnvironment jazzRuntimeEnvironment,
      final JazzCore<PropertiesJazzConfiguration> jazzCore) throws Exception
  {
    if (websocketService != null)
    {
      websocketService.stop();
    }
  }
}

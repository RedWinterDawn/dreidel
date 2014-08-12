package com.jive.qa.dreidel.goyim.jinst.service.rest;

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
import com.jive.qa.dreidel.goyim.jinst.service.ExampleServiceModule;

public class Main extends SimpleAbstractJazzRuntimeLauncher
{

  private Injector injector;
  private RestServerManager manager;

  @Override
  public String getDefaultServiceName()
  {
    return "goyim-jinst-1";
  }

  public static void main(final String[] args)
  {
    final Main main = new Main();
    main.launch(args);
  }

  @Override
  protected void postInit(final JazzRuntimeEnvironment jazzRuntimeEnvironment) throws Exception
  {
    injector = Guice.createInjector(new ExampleServiceModule(), new AbstractModule()
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
    manager =
        new RestServerManager(jazzRuntimeEnvironment, jazzCore.getRestServiceManager().get(),
            injector.getInstance(GoyimView.class),
            injector.getInstance(ObjectMapper.class), injector.getInstance(Key.get(Integer.class,
                Names.named("rest-server-port"))));
    manager.start();
  }

  @Override
  protected void preStop(final JazzRuntimeEnvironment jazzRuntimeEnvironment,
      final JazzCore<PropertiesJazzConfiguration> jazzCore) throws Exception
  {
    if (manager != null)
    {
      manager.stop();
    }
  }

}

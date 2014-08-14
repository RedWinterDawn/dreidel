package com.jive.qa.dreidel.goyim.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Names;
import com.jive.myco.jazz.api.runtime.JazzRuntimeEnvironment;
import com.jive.qa.dreidel.goyim.views.DreidelView;
import com.jive.v5.runtime.Configuration;
import com.jive.v5.runtime.Environment;
import com.jive.v5.runtime.RestClientEnvironment;
import com.jive.v5.runtime.SimpleAbstractV5Runner;

public class GoyimLauncher extends SimpleAbstractV5Runner

{

  public GoyimLauncher()
  {
    super("dreidel-goyim");
  }

  Injector injector;
  private RestServerManager dreidel;
  private JazzRuntimeEnvironment jazzEnvironment;

  public static void main(final String[] args)
  {
    new GoyimLauncher().launch(args);
  }

  @Override
  protected void postInit(JazzRuntimeEnvironment jazzRuntimeEnvironment) throws Exception
  {
    super.postInit(jazzRuntimeEnvironment);
    jazzEnvironment = jazzRuntimeEnvironment;

  }

  @Override
  public void run(Configuration conf, Environment<Configuration> env) throws Exception
  {

    injector = Guice.createInjector(new RestModule(), new AbstractModule()
    {
      @Override
      protected void configure()
      {
        Names.bindProperties(binder(), env.properties().get());
        bind(RestClientEnvironment.class).toInstance(env.rest());
      }
    });

    dreidel = new RestServerManager(jazzEnvironment, env.jazzCore().getRestServiceManager().get(),
        injector.getInstance(DreidelView.class),
        injector.getInstance(ObjectMapper.class),
        injector.getInstance(Key.get(Integer.class,
            Names.named("rest-server-port"))));
    dreidel.start();
  }
}

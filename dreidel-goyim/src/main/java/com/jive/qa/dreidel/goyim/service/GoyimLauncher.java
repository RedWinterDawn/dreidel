package com.jive.qa.dreidel.goyim.service;

import com.jive.qa.dreidel.goyim.views.DreidelView;
import com.jive.v5.runtime.Bootstrap;
import com.jive.v5.runtime.Configuration;
import com.jive.v5.runtime.Environment;
import com.jive.v5.runtime.SimpleAbstractV5Runner;
import com.jive.v5.runtime.guice.GuiceRuntimePlugin;

public class GoyimLauncher extends SimpleAbstractV5Runner

{

  private GuiceRuntimePlugin plugin;

  @Override
  public void initialize(final Bootstrap<Configuration> bootstrap)
  {
    plugin = GuiceRuntimePlugin.newBuilder().addModule(new RestModule()).build();
    bootstrap.addBundle(plugin);
  }

  public GoyimLauncher()
  {
    super("DreidelGoyim");
  }

  @Override
  public void run(final Configuration conf, final Environment<Configuration> env) throws Exception
  {
    final DreidelView dreidel = plugin.getInjector().getInstance(DreidelView.class);
    env.jaxrs("goyim").register(dreidel);
  }

  public static void main(final String[] args)
  {
    new GoyimLauncher().run(args);
  }

}

package com.jive.qa.dreidel.goyim.service;

import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.util.Modules;
import com.jive.ftw.rest.DefaultRestServerBindings;
import com.jive.ftw.rest.RestServerLauncher;

/**
 * Example of a module that boostsraps the classes used by the Jazz Service.
 * 
 * @author David Valeri
 */
public class ServiceModule extends AbstractModule
{

  @Override
  protected void configure()
  {
    Module module = Modules.override(new DefaultRestServerBindings()).with(new RestModule());
    install(module);
    bind(RestServerLauncher.class).asEagerSingleton();
  }

}

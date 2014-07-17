package com.jive.qa.dreidel.goyim.jinst.service;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Named;

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
    return "http://10.20.27.83:8019";
  }
}

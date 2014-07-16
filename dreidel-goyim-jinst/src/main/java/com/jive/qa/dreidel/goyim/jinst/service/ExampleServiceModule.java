package com.jive.qa.dreidel.goyim.jinst.service;

import com.google.inject.AbstractModule;

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
    // Don't need to use singleton here as the class is annotated.
    // Bind the example service class as a singleton.
  }
}

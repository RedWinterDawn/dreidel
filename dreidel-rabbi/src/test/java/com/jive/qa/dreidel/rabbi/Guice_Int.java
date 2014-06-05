package com.jive.qa.dreidel.rabbi;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.junit.Test;

import com.jive.myco.jazz.config.inject.JazzServiceConfigurationModule;
import com.jive.myco.jazz.test.launcher.JazzServiceTestLauncher;
import com.jive.qa.dreidel.rabbi.service.WebsocketServiceModule;

public class Guice_Int
{
  @Test
  public void test() throws Exception
  {
    try (
        // Implements closeable so you can be sure it gets killed on the way out of your test
        // method.

        final JazzServiceTestLauncher launcher = JazzServiceTestLauncher.builder()
            // Name it
            .serviceName("jazz-examples-jazz-service")
            // Add the modules you want to launch
            .withModule(JazzServiceConfigurationModule.class)
            .withModule(WebsocketServiceModule.class)
            // Set some properties provided to your service via the launcher
            .build())
    {
      // Start it up
      launcher.init();

      // Wait a little so it spits out some logs

      new BufferedReader(new InputStreamReader(System.in)).read();
    }
  }
}

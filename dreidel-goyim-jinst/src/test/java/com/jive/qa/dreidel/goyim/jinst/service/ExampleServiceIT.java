package com.jive.qa.dreidel.goyim.jinst.service;

import org.junit.Test;

import com.jive.myco.jazz.config.inject.JazzServiceConfigurationModule;
import com.jive.myco.jazz.test.launcher.JazzServiceTestLauncher;

/**
 * Example of of a local integration test that uses the {@link JazzServiceTestLauncher test
 * launcher}.
 *
 * @author David Valeri
 */
public class ExampleServiceIT
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
            .addModule(ExampleServiceModule.class)
            .addModule(JazzServiceConfigurationModule.class)
            // Set some properties provided to your service via the launcher
            .addProperty("arg1", "Au revoir!")
            .build())
    {
      // Start it up
      launcher.init();

      // Wait a little so it spits out some logs
      Thread.sleep(2000);
    }
  }
}

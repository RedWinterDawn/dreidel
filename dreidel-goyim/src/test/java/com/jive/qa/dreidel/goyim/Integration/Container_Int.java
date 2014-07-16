package com.jive.qa.dreidel.goyim.Integration;

import org.junit.Test;

import com.jive.myco.jazz.config.inject.JazzServiceConfigurationModule;
import com.jive.myco.jazz.test.launcher.JazzServiceTestLauncher;
import com.jive.qa.dreidel.goyim.service.ServiceModule;

/**
 * Example of of a local integration test that uses the {@link JazzServiceTestLauncher test
 * launcher}.
 *
 * @author David Valeri
 */
public class Container_Int
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
            .addModule(ServiceModule.class)
            .addModule(JazzServiceConfigurationModule.class)
            .build())
    {
      // Start it up
      launcher.init();
      while (true)
      {

      }
    }
  }
}

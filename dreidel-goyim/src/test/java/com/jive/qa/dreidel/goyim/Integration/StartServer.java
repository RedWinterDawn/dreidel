package com.jive.qa.dreidel.goyim.Integration;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import com.jive.myco.jazz.config.inject.JazzServiceConfigurationModule;
import com.jive.myco.jazz.test.launcher.JazzServiceTestLauncher;
import com.jive.qa.dreidel.goyim.service.ServiceModule;

public class StartServer
{

  protected static JazzServiceTestLauncher launcher;

  @BeforeClass
  public static void startServer() throws InterruptedException
  {
    launcher = JazzServiceTestLauncher.builder()
        // Name it
        .serviceName("jazz-examples-jazz-service")
        // Add the modules you want to launch
        .addModule(ServiceModule.class)
        .addModule(JazzServiceConfigurationModule.class)
        .build();
    launcher.init();
    Thread.sleep(1000);
  }

  @AfterClass
  public static void stopServer()
  {
    launcher.destroy();
  }
}

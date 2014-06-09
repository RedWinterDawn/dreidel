package com.jive.qa.dreidel.spinnit.postgres.integration;

import java.io.IOException;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import com.jive.myco.jazz.config.inject.JazzServiceConfigurationModule;
import com.jive.myco.jazz.test.launcher.JazzServiceTestLauncher;
import com.jive.qa.dreidel.rabbi.service.WebsocketServiceModule;

public class ServerStartup
{

  static JazzServiceTestLauncher launcher;

  @BeforeClass
  public static void SetupServer() throws IOException
  {
    launcher = JazzServiceTestLauncher.builder()
        // Name it
        .serviceName("jazz-examples-jazz-service")
        // Add the modules you want to launch
        .withModule(JazzServiceConfigurationModule.class)
        .withModule(WebsocketServiceModule.class)
        // Set some properties provided to your service via the launcher
        .build();
    // Start it up
    launcher.init();
  }

  @AfterClass
  public static void TearDownServer() throws IOException
  {
    launcher.close();
  }

}

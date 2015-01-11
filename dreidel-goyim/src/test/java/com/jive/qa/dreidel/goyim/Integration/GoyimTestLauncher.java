package com.jive.qa.dreidel.goyim.Integration;

import com.jive.myco.config.PropertiesJazzConfiguration;
import com.jive.myco.jazz.test.launcher.JazzRuntimeTestLauncher;
import com.jive.qa.dreidel.goyim.service.GoyimLauncher;

public final class GoyimTestLauncher
{

  public static void main(String[] args) throws Exception
  {
    JazzRuntimeTestLauncher.<PropertiesJazzConfiguration> builder()
        .launcher(new GoyimLauncher())
        .serviceName("local-dreidel")
        .build()
        .init();

    Thread.sleep(Long.MAX_VALUE);
  }

}

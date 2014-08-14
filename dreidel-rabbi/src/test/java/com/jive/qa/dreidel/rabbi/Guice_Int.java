package com.jive.qa.dreidel.rabbi;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.junit.Test;

import com.jive.myco.config.PropertiesJazzConfiguration;
import com.jive.myco.jazz.test.launcher.JazzRuntimeTestLauncher;
import com.jive.qa.dreidel.rabbi.service.Main;

public class Guice_Int
{
  @Test
  public void test() throws Exception
  {
    final Main main = new Main();

    try (JazzRuntimeTestLauncher<PropertiesJazzConfiguration> testLauncher =
        JazzRuntimeTestLauncher.<PropertiesJazzConfiguration> builder()
        .launcher(main)
        .build())
    {
      testLauncher.init();

      // Wait a little so it spits out some logs

      new BufferedReader(new InputStreamReader(System.in)).read();
    }
  }
}

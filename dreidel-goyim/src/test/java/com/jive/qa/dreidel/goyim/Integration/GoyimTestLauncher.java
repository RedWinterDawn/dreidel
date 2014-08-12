package com.jive.qa.dreidel.goyim.Integration;

import org.junit.Test;

import com.jive.qa.dreidel.goyim.service.GoyimLauncher;
import com.jive.v5.runtime.test.V5RunnerTestLauncher;

public class GoyimTestLauncher
{

  @Test
  public void main() throws Exception
  {
    V5RunnerTestLauncher.builder()
        .launcher(new GoyimLauncher())
        .serviceName("local-dreidel")
        .build()
        .init();

    Thread.sleep(Long.MAX_VALUE);
  }

}

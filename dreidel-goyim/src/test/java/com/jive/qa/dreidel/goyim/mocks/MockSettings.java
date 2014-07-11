package com.jive.qa.dreidel.goyim.mocks;

import java.util.Map;

import com.google.common.collect.Maps;
import com.jive.qa.dreidel.goyim.service.BmSettings;
import com.jive.qa.dreidel.goyim.service.JimSettings;

public class MockSettings
{

  public static BmSettings getBm()
  {
    Map<String, String> networks = Maps.newConcurrentMap();
    networks.put("dev", "10.20.26.");
    networks.put("vmcontrolorm", "10.31.29.");

    return new BmSettings(4, 8, 19, networks, "ops-1a", 14, "dev", 200, 220);
  }

  public static JimSettings getJim()
  {

    return new JimSettings("asefdasd", "localhost", 3000, "api");
  }
}

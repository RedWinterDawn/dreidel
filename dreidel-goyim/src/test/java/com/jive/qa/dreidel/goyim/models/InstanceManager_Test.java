package com.jive.qa.dreidel.goyim.models;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jive.qa.dreidel.goyim.controllers.InstanceManager;
import com.jive.qa.dreidel.goyim.service.BmSettings;
import com.jive.qa.dreidel.goyim.service.JimSettings;

public class InstanceManager_Test
{

  private static BmSettings bmSettings;
  private static JimSettings jimSettings;

  @BeforeClass
  public static void setup()
  {
    Map<String, String> networks = Maps.newConcurrentMap();
    networks.put("dev", "10.20.26.");
    networks.put("vmcontrolorm", "10.31.29.");
    bmSettings = new BmSettings(4, 8, 19, networks, "ops-1a", 14);
    jimSettings = new JimSettings("asefdasd", "localhost", 3000, "api");
  }

  @Test
  public void SuccessiveGetsReturns_SuccessiveResults()
  {
    InstanceManager instanceManager =
        new InstanceManager(bmSettings, jimSettings);

    Instance i1 = instanceManager.getNextAvailableInstance();
    Instance i2 = instanceManager.getNextAvailableInstance();
    Instance i3 = instanceManager.getNextAvailableInstance();

    assertEquals(0, i1.getInstance());
    assertEquals(1, i2.getInstance());
    assertEquals(2, i3.getInstance());
  }

  @Test
  public void SuccessiveGetsWithRemoveInMiddle_GetsTheMiddleNumber()
  {
    InstanceManager instanceManager = new InstanceManager(bmSettings, jimSettings);

    Instance i1 = instanceManager.getNextAvailableInstance();
    Instance i2 = instanceManager.getNextAvailableInstance();
    Instance i3 = instanceManager.getNextAvailableInstance();

    instanceManager.removeInstance(1);

    Instance i4 = instanceManager.getNextAvailableInstance();

    assertEquals(0, i1.getInstance());
    assertEquals(1, i2.getInstance());
    assertEquals(2, i3.getInstance());
    assertEquals(1, i4.getInstance());
  }

  @Test
  public void SuccesiveGetsGetCorrectCpus_GetsHyperThreadedCpus()
  {
    InstanceManager instanceManager = new InstanceManager(bmSettings, jimSettings);

    List<Instance> instances = Lists.newArrayList();

    for (int i = 0; i < 10; i++)
    {
      instances.add(instanceManager.getNextAvailableInstance());
    }

    // normal cpus
    assertEquals(Integer.valueOf(4), instances.get(0).getCpuset().get(0));
    assertEquals(Integer.valueOf(5), instances.get(1).getCpuset().get(0));
    assertEquals(Integer.valueOf(6), instances.get(2).getCpuset().get(0));
    assertEquals(Integer.valueOf(7), instances.get(3).getCpuset().get(0));
    assertEquals(Integer.valueOf(8), instances.get(4).getCpuset().get(0));

    // hyper-threaded cpus
    assertEquals(Integer.valueOf(23), instances.get(5).getCpuset().get(0));
    assertEquals(Integer.valueOf(24), instances.get(6).getCpuset().get(0));
    assertEquals(Integer.valueOf(25), instances.get(7).getCpuset().get(0));
    assertEquals(Integer.valueOf(26), instances.get(8).getCpuset().get(0));
    assertEquals(Integer.valueOf(27), instances.get(9).getCpuset().get(0));

  }

  @Test
  public void SuccessiveGets_GetsCorrectIps()
  {

    InstanceManager instanceManager = new InstanceManager(bmSettings, jimSettings);

    List<Instance> instances = Lists.newArrayList();

    for (int i = 0; i < 3; i++)
    {
      instances.add(instanceManager.getNextAvailableInstance());
    }

    for (int i = 0; i < 3; i++)
    {
      for (int j = 0; j < 2; j++)
      {
        if (instances.get(i).getNetworks().get(j).getName().equals("dev"))
        {
          assertEquals("10.20.26." + i, instances.get(i).getNetworks().get(j).getAddress());
        }
        else
        {
          assertEquals("10.31.29." + i, instances.get(i).getNetworks().get(j).getAddress());
        }
      }
    }

  }

}

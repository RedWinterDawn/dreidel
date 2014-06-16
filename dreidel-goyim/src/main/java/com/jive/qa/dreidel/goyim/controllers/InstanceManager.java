package com.jive.qa.dreidel.goyim.controllers;

import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import javax.inject.Inject;

import lombok.Synchronized;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.jive.qa.dreidel.goyim.models.Instance;
import com.jive.qa.dreidel.goyim.models.Network;
import com.jive.qa.dreidel.goyim.service.BmSettings;
import com.jive.qa.dreidel.goyim.service.JimSettings;

public class InstanceManager
{
  private static final int DOUBLE_TO_ALLOW_HYPERTHREADING = 2;

  public final Set<Integer> instances;
  public final BmSettings bmSettings;
  public final JimSettings jimSettings;

  @Inject
  public InstanceManager(BmSettings bmSettings, JimSettings jimSettings)
  {
    instances = Sets.newConcurrentHashSet();
    this.bmSettings = bmSettings;
    this.jimSettings = jimSettings;
  }

  @Synchronized
  public Instance getNextAvailableInstance()
  {
    int i = 0;
    for (; i < instances.size(); i++)
    {
      if (!instances.contains(i))
      {
        break;
      }
    }

    List<Network> networks = Lists.newArrayList();
    for (Entry<String, String> entry : bmSettings.getNetworksMap().entrySet())
    {
      networks.add(new Network(entry.getKey(), entry.getValue() + i));
    }

    Instance rtn =
        new Instance(bmSettings.getSite(), bmSettings.getBlade(),
            Lists.newArrayList(getCpuFromInstanceId(i)), i, networks);
    instances.add(i);
    return rtn;
  }

  @Synchronized
  public void removeInstance(int i)
  {
    instances.remove(i);
  }

  private int getCpuFromInstanceId(int id)
  {
    // get range of cpus
    int range = (bmSettings.getCpuStop() - bmSettings.getCpuStart()) + 1;
    // get a number between 0 and the max number of cpus * 2
    int cpuNumber =
        id % (range * DOUBLE_TO_ALLOW_HYPERTHREADING);

    int rtn = -1;
    if (cpuNumber < range)
    {
      // it should be a normal cpu
      // add the cpu start so it is no longer 0 based but start based.
      rtn = cpuNumber + bmSettings.getCpuStart();
    }
    else
    {
      // it should be a hyper threaded cpu
      // subtract the range so it is inside the range of cpus.
      // add the cpu start so it is no longer 0 based but start based.
      // add the last cpu so it is a hyper threaded cpu
      rtn = (cpuNumber - range) + bmSettings.getCpuStart() + bmSettings.getLastCpu();
    }
    return rtn;
  }

}

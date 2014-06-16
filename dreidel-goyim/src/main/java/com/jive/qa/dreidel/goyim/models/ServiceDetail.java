package com.jive.qa.dreidel.goyim.models;

import java.beans.ConstructorProperties;
import java.util.List;

public class ServiceDetail extends Service
{
  private List<Disk> disks;
  private List<String> networks;

  @ConstructorProperties({ "name", "memory", "cpus", "classes", "disks", "networks" })
  public ServiceDetail(String name, int memory, int cpus, List<String> classes, List<Disk> disks,
      List<String> networks)
  {
    super(name, memory, cpus, classes);
    this.disks = disks;
    this.networks = networks;
  }

}

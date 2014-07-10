package com.jive.qa.dreidel.goyim.models;

import java.beans.ConstructorProperties;
import java.util.List;

import lombok.Getter;

@Getter
public class ServiceDetail extends Service
{
  private final List<Disk> disks;
  private final List<String> networks;

  @ConstructorProperties({ "name", "memory", "cpus", "classes", "disks", "networks" })
  public ServiceDetail(String name, int memory, int cpus, List<String> classes, List<Disk> disks,
      List<String> networks)
  {
    super(name, memory, cpus, classes);
    this.disks = disks;
    this.networks = networks;
  }

}

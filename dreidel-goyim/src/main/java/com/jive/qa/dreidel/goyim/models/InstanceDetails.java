package com.jive.qa.dreidel.goyim.models;

import java.beans.ConstructorProperties;
import java.util.Set;

import lombok.Getter;

@Getter
public class InstanceDetails extends Instance
{
  private final String created_at;
  private final String updated_at;
  private final int instance;
  private final String rid;
  private final int bm;
  private final Set<Integer> cpuset;
  private final Set<Network> networks;
  private final String id;

  @ConstructorProperties({ "site", "service", "branch", "created_at", "updated_at", "instance",
      "rid", "bm", "cpuset", "networks", "id" })
  public InstanceDetails(final String site, final String service, final String branch,
      final String created_at,
      final String updated_at, final int instance, final String rid, final int bm,
      final Set<Integer> cpuset,
      final Set<Network> networks, final String id)
  {
    super(site, service, branch);
    this.created_at = created_at;
    this.updated_at = updated_at;
    this.instance = instance;
    this.rid = rid;
    this.bm = bm;
    this.cpuset = cpuset;
    this.networks = networks;
    this.id = id;
  }

}

package com.jive.qa.dreidel.goyim.service;

import java.util.Map;

import javax.inject.Named;

import lombok.Getter;

import com.google.inject.Inject;

@Getter
public class BmSettings
{
  @Inject
  public BmSettings(@Named("bm.cpu.start") final int cpuStart,
      @Named("bm.cpu.stop") final int cpuStop, @Named("bm.cpu.physical.last") final int lastCpu,
      @Named("networksMap") final Map<String, String> networksMap, @Named("bm.site") final String site,
      @Named("bm.blade") final int blade, @Named("bm.networks.preferred") final String preferred,
      @Named("bm.iprange.start") final int ipRangeStart, @Named("bm.iprange.stop") final int ipRangeStop)
  {
    this.cpuStart = cpuStart;
    this.cpuStop = cpuStop;
    this.lastCpu = lastCpu;
    this.networksMap = networksMap;
    this.site = site;
    this.blade = blade;
    this.preferred = preferred;
    this.ipRangeStart = ipRangeStart;
    this.ipRangeStop = ipRangeStop;
  }

  private final int blade;
  private final String site;
  private final Map<String, String> networksMap;
  private final int lastCpu;
  private final int cpuStart;
  private final int cpuStop;
  private final String preferred;
  private final int ipRangeStart;
  private final int ipRangeStop;

}

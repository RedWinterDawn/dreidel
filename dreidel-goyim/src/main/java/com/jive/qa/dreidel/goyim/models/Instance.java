package com.jive.qa.dreidel.goyim.models;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Instance
{
  private final String site;
  private final int bm;
  private final List<Integer> cpuset;
  private final int instance;
  private final List<Network> networks;

}

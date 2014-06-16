package com.jive.qa.dreidel.goyim.models;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Instance
{
  private String site;
  private int bm;
  private List<Integer> cpuset;
  private int instance;
  private List<Network> networks;

}

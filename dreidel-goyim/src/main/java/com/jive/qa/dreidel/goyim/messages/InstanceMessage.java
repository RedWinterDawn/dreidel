package com.jive.qa.dreidel.goyim.messages;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

import com.jive.qa.dreidel.goyim.models.Network;

@AllArgsConstructor
@Getter
public class InstanceMessage extends JimMessage
{
  private String site;
  private int bm;
  private List<Integer> cpuset;
  private int instance;
  private List<Network> networks;
  private String service;

}

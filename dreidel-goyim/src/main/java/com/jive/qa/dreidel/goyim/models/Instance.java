package com.jive.qa.dreidel.goyim.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Instance
{
  private final String site;
  private final String service;
  private final String branch;

}

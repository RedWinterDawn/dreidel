package com.jive.qa.dreidel.goyim.models;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Service
{
  private final String name;
  private final int memory;
  private final int cpus;
  private final List<String> classes;
}

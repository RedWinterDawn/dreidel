package com.jive.qa.dreidel.goyim.models;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class Service
{
  private String name;
  private int memory;
  private int cpus;
  private final List<String> classes;
}

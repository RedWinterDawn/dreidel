package com.jive.qa.dreidel.goyim.messages;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

import com.jive.qa.dreidel.goyim.models.Service;

@AllArgsConstructor
@Getter
public class ServiceListMessage extends JimMessage
{
  List<Service> services;
}

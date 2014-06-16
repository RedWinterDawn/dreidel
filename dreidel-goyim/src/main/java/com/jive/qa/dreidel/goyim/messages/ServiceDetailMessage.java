package com.jive.qa.dreidel.goyim.messages;

import lombok.AllArgsConstructor;
import lombok.Getter;

import com.jive.qa.dreidel.goyim.models.ServiceDetail;

@AllArgsConstructor
@Getter
public class ServiceDetailMessage extends JimMessage
{
  private final ServiceDetail service;
}

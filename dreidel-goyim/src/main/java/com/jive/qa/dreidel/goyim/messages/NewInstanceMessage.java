package com.jive.qa.dreidel.goyim.messages;

import lombok.AllArgsConstructor;
import lombok.Getter;

import com.jive.qa.dreidel.goyim.models.Instance;

@AllArgsConstructor
@Getter
public class NewInstanceMessage extends JimMessage
{
  private final Instance instance;
}

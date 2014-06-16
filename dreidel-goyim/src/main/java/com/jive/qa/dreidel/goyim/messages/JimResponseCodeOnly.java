package com.jive.qa.dreidel.goyim.messages;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class JimResponseCodeOnly extends JimMessage
{
  private final int responseCode;
}

package com.jive.qa.dreidel.goyim.messages;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class JimErrorMessage extends JimMessage
{
  private int responseCode;
  private String responseMessage;
}

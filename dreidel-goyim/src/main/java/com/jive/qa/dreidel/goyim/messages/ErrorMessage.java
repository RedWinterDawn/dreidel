package com.jive.qa.dreidel.goyim.messages;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ErrorMessage
{
  private final String type;
  private final String message;
}

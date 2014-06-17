package com.jive.qa.dreidel.goyim.messages;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class IdResponse
{
  private final int id;
  private final String address;

}

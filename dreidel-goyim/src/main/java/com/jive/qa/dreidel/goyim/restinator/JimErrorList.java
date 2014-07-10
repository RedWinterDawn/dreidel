package com.jive.qa.dreidel.goyim.restinator;

import java.util.List;

import lombok.Getter;

import com.jive.qa.dreidel.goyim.messages.JimMessage;

@Getter
public class JimErrorList extends JimMessage
{

  private final List<String> errors;

  public JimErrorList(List<String> errors)
  {
    this.errors = errors;
  }

}

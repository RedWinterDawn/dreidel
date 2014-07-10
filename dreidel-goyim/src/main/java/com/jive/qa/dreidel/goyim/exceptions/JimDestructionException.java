package com.jive.qa.dreidel.goyim.exceptions;

public class JimDestructionException extends Exception
{
  private static final long serialVersionUID = 1L;

  public JimDestructionException(String message)
  {
    super(message);
  }

  public JimDestructionException(String message, Throwable cause)
  {
    super(message, cause);
  }

}

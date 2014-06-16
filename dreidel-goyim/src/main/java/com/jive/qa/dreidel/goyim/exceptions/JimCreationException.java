package com.jive.qa.dreidel.goyim.exceptions;

public class JimCreationException extends Exception
{
  private static final long serialVersionUID = 1L;

  public JimCreationException(String message)
  {
    super(message);
  }

  public JimCreationException(String message, Throwable cause)
  {
    super(message, cause);
  }

}

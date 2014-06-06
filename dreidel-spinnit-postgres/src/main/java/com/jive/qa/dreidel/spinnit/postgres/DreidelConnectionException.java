package com.jive.qa.dreidel.spinnit.postgres;

public class DreidelConnectionException extends Exception
{
  private static final long serialVersionUID = 1L;

  public DreidelConnectionException(String message)
  {
    super(message);
  }

  public DreidelConnectionException(String message, Throwable cause)
  {
    super(message, cause);
  }
}

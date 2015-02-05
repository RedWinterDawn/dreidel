package com.jive.qa.dreidel.spinnit.api;

import java.io.IOException;

public class DreidelConnectionException extends IOException
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

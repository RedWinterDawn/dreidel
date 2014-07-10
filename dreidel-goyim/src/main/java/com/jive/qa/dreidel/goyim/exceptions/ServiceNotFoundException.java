package com.jive.qa.dreidel.goyim.exceptions;

public class ServiceNotFoundException extends Exception
{

  private static final long serialVersionUID = 1L;

  public ServiceNotFoundException(String message)
  {
    super(message);

  }

  public ServiceNotFoundException(String message, Throwable cause)
  {
    super(message, cause);
  }

}

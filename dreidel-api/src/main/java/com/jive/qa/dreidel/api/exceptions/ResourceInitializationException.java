package com.jive.qa.dreidel.api.exceptions;

public class ResourceInitializationException extends Exception
{

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  public ResourceInitializationException()
  {
  }

  public ResourceInitializationException(final String message)
  {
    super(message);
  }

  public ResourceInitializationException(final String message, final Throwable cause)
  {
    super(message, cause);
  }

}

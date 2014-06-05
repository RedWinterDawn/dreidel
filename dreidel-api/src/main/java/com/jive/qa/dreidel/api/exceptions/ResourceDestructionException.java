package com.jive.qa.dreidel.api.exceptions;

public class ResourceDestructionException extends Exception
{

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  public ResourceDestructionException(final String message)
  {
    super(message);
  }

  public ResourceDestructionException(final String message, final Throwable cause)
  {
    super(message, cause);
  }

}

package com.jive.qa.dreidel.api.exceptions;

public class MessageDecodeException extends Exception
{
  private static final long serialVersionUID = 1L;

  public MessageDecodeException()
  {
    super();
  }

  public MessageDecodeException(final String message)
  {
    super(message);
  }

  public MessageDecodeException(final String message, final Throwable cause)
  {
    super(message, cause);
  }

  public MessageDecodeException(final Throwable cause)
  {
    super(cause);
  }
}

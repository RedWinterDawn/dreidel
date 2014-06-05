package com.jive.qa.dreidel.api.exceptions;

@SuppressWarnings("serial")
public class MessageDecodeException extends Exception
{
  

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

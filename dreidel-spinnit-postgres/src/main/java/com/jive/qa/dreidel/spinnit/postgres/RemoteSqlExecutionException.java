package com.jive.qa.dreidel.spinnit.postgres;

@SuppressWarnings("serial")
public class RemoteSqlExecutionException extends RuntimeException
{
  public RemoteSqlExecutionException(String message)
  {
    super(message);
  }

  public RemoteSqlExecutionException(String message, Exception ex)
  {
    super(message, ex);
  }

  public RemoteSqlExecutionException(Exception ex)
  {
    super(ex);
  }
}

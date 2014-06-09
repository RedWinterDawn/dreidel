package com.jive.qa.dreidel.spinnit.postgres;

public class RemoteSqlExecutionException extends RuntimeException
{
  private static final long serialVersionUID = 1L;

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

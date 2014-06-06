package com.jive.qa.dreidel.rabbi;

/**
 * 
 * @author jdavidson
 *
 */
public class SQLFileLoadException extends Exception
{
  private static final long serialVersionUID = 1L;

  public SQLFileLoadException(final String msg)
  {
    super(msg);
  }

  public SQLFileLoadException(final String msg, final Throwable thr)
  {
    super(msg, thr);
  }

}

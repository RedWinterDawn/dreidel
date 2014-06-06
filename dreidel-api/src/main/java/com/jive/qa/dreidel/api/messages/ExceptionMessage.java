package com.jive.qa.dreidel.api.messages;

import java.beans.ConstructorProperties;

import lombok.Getter;

/**
 * the message used to send an exception back to the user
 * 
 * @author jdavidson
 *
 */
@Getter
public class ExceptionMessage extends ReplyMessage
{
  protected final String exceptionType;
  protected final String exceptionMessage;

  @ConstructorProperties({ "exceptionType", "exceptionMessage", "referenceId" })
  public ExceptionMessage(final String exceptionType, final String exceptionMessage,
      final String referenceId)
  {
    super(referenceId);
    this.exceptionType = exceptionType;
    this.exceptionMessage = exceptionMessage;
  }

  public ExceptionMessage(final Throwable e, final String referenceId)
  {
    super(referenceId);
    exceptionType = e.getClass().getSimpleName();
    exceptionMessage = e.getMessage();
  }

}

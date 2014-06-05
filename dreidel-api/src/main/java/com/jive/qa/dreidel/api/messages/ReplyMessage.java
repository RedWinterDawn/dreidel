package com.jive.qa.dreidel.api.messages;

import java.beans.ConstructorProperties;

import lombok.Getter;

@Getter
public abstract class ReplyMessage extends TwoWayMessage
{
  @ConstructorProperties({ "referenceId" })
  public ReplyMessage(final String referenceId)
  {
    super(referenceId);
  }
}

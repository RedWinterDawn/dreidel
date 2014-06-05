package com.jive.qa.dreidel.api.messages;

import java.beans.ConstructorProperties;

import lombok.Getter;

@Getter
public abstract class TwoWayMessage extends Message
{
  protected final String referenceId;

  @ConstructorProperties({ "referenceId" })
  public TwoWayMessage(final String referenceId)
  {
    this.referenceId = referenceId;
  }

}

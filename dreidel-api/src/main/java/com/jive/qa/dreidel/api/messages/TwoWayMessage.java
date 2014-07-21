package com.jive.qa.dreidel.api.messages;

import java.beans.ConstructorProperties;

import lombok.Getter;

/**
 * The abstract parent for all two way messages
 *
 * @author jdavidson
 *
 */
@Getter
public abstract class TwoWayMessage extends Message
{
  protected final String referenceId;
  protected final String direction;

  @ConstructorProperties({ "referenceId" })
  public TwoWayMessage(final String referenceId, final String direction)
  {
    this.referenceId = referenceId;
    this.direction = direction;
  }

}

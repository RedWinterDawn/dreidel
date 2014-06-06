package com.jive.qa.dreidel.api.messages;

import lombok.Getter;

/**
 * the abstract parent for all messages
 * 
 * @author jdavidson
 *
 */
@Getter
public abstract class Message
{
  protected final String type;

  public Message()
  {
    type = this.getClass().getSimpleName();
  }

}

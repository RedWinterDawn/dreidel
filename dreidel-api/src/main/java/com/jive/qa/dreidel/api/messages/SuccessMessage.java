package com.jive.qa.dreidel.api.messages;

import java.beans.ConstructorProperties;

public class SuccessMessage extends ReplyMessage
{

  @ConstructorProperties({ "referenceId" })
  public SuccessMessage(final String referenceId)
  {
    super(referenceId);
  }

}

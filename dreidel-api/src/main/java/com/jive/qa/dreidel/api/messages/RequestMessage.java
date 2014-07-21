package com.jive.qa.dreidel.api.messages;

import java.beans.ConstructorProperties;

import lombok.Getter;

import com.jive.qa.dreidel.api.interfaces.MessageCategoryVisitable;
import com.jive.qa.dreidel.api.replies.Reply;

/**
 * the abstract parent of all request messages. this is visitable.
 *
 * @author jdavidson
 *
 */
@Getter
public abstract class RequestMessage extends TwoWayMessage implements
    MessageCategoryVisitable<Reply, VisitorContext>
{

  @ConstructorProperties({ "referenceId" })
  public RequestMessage(final String referenceId)
  {
    super(referenceId, "request");
  }
}

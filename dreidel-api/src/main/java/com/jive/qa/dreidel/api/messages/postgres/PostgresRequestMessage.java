package com.jive.qa.dreidel.api.messages.postgres;

import java.beans.ConstructorProperties;

import lombok.NonNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jive.myco.commons.concurrent.PnkyPromise;
import com.jive.qa.dreidel.api.interfaces.MessageCategoryVisitor;
import com.jive.qa.dreidel.api.interfaces.PostgresVisitable;
import com.jive.qa.dreidel.api.messages.RequestMessage;
import com.jive.qa.dreidel.api.messages.VisitorContext;
import com.jive.qa.dreidel.api.replies.Reply;

/**
 * the abstract parent of all postgres messages
 * 
 * @author jdavidson
 *
 */
public abstract class PostgresRequestMessage extends RequestMessage implements
    PostgresVisitable<Reply, VisitorContext>
{

  @ConstructorProperties({ "referenceId" })
  public PostgresRequestMessage(@NonNull final String referenceId)
  {
    super(referenceId);
  }

  @Override
  @JsonIgnore
  public PnkyPromise<Reply> accept(
      final MessageCategoryVisitor<Reply, VisitorContext> visitor,
      final VisitorContext context)
  {
    return visitor.visit(this, context);
  }
}

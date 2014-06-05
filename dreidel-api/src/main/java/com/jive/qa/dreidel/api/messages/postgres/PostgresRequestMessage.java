package com.jive.qa.dreidel.api.messages.postgres;

import java.beans.ConstructorProperties;

import com.jive.myco.commons.callbacks.ChainedFuture;
import com.jive.qa.dreidel.api.interfaces.MessageCategoryVisitor;
import com.jive.qa.dreidel.api.interfaces.PostgresVisitable;
import com.jive.qa.dreidel.api.messages.RequestMessage;
import com.jive.qa.dreidel.api.messages.VisitorContext;
import com.jive.qa.dreidel.api.replies.Reply;

public abstract class PostgresRequestMessage extends RequestMessage implements
    PostgresVisitable<Reply, VisitorContext>
{

  @ConstructorProperties({ "referenceId" })
  public PostgresRequestMessage(final String referenceId)
  {
    super(referenceId);
  }

  @Override
  public ChainedFuture<Reply> accept(
      final MessageCategoryVisitor<Reply, VisitorContext> visitor,
      final VisitorContext context)
  {
    return visitor.visit(this, context);
  }
}

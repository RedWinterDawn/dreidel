package com.jive.qa.dreidel.api.messages.postgres;

import java.beans.ConstructorProperties;

import com.jive.myco.commons.callbacks.ChainedFuture;
import com.jive.qa.dreidel.api.interfaces.PostgresVisitor;
import com.jive.qa.dreidel.api.messages.VisitorContext;
import com.jive.qa.dreidel.api.replies.Reply;

public class PostgresCreateMessage extends PostgresRequestMessage
{

  @ConstructorProperties({ "referenceId" })
  public PostgresCreateMessage(final String referenceId)
  {
    super(referenceId);
    // TODO Auto-generated constructor stub
  }

  @Override
  public ChainedFuture<Reply> accept(
      final PostgresVisitor<Reply, VisitorContext> visitor,
      final VisitorContext context)
  {
    return visitor.visit(this, context);
  }

}

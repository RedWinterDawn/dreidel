package com.jive.qa.dreidel.rabbi.visitors;

import javax.inject.Inject;

import lombok.AllArgsConstructor;

import com.jive.myco.commons.concurrent.PnkyPromise;
import com.jive.qa.dreidel.api.interfaces.JinstVisitor;
import com.jive.qa.dreidel.api.interfaces.MessageCategoryVisitor;
import com.jive.qa.dreidel.api.interfaces.PostgresVisitor;
import com.jive.qa.dreidel.api.messages.VisitorContext;
import com.jive.qa.dreidel.api.messages.jinst.JinstRequestMessage;
import com.jive.qa.dreidel.api.messages.postgres.PostgresRequestMessage;
import com.jive.qa.dreidel.api.replies.Reply;

/**
 * routes messages to other visitors.
 *
 * @author jdavidson
 *
 */
@AllArgsConstructor(onConstructor = @__(@Inject))
public class RoutingVisitor implements
    MessageCategoryVisitor<Reply, VisitorContext>
{

  private final PostgresVisitor<Reply, VisitorContext> postgresVisitor;
  private final JinstVisitor<Reply, VisitorContext> jinstVisitor;

  @Override
  public PnkyPromise<Reply> visit(final PostgresRequestMessage message,
      final VisitorContext context)
  {
    return message.accept(postgresVisitor, context);
  }

  @Override
  public PnkyPromise<Reply> visit(JinstRequestMessage message, VisitorContext context)
  {
    return message.accept(jinstVisitor, context);
  }
}

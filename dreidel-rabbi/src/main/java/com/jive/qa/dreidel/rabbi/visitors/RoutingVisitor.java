package com.jive.qa.dreidel.rabbi.visitors;

import javax.inject.Inject;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.jive.myco.commons.callbacks.ChainedFuture;
import com.jive.qa.dreidel.api.interfaces.MessageCategoryVisitor;
import com.jive.qa.dreidel.api.interfaces.PostgresVisitor;
import com.jive.qa.dreidel.api.messages.VisitorContext;
import com.jive.qa.dreidel.api.messages.postgres.PostgresRequestMessage;
import com.jive.qa.dreidel.api.replies.Reply;

/**
 * routes messages to other visitors.
 * 
 * @author jdavidson
 *
 */
@Slf4j
@AllArgsConstructor(onConstructor = @__(@Inject))
public class RoutingVisitor implements
    MessageCategoryVisitor<Reply, VisitorContext>
{

  private final PostgresVisitor<Reply, VisitorContext> postgresVisitor;

  @Override
  public ChainedFuture<Reply> visit(final PostgresRequestMessage message,
      final VisitorContext context)
  {
    return message.accept(postgresVisitor, context);
  }

}

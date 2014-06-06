package com.jive.qa.dreidel.api.interfaces;

import com.jive.myco.commons.callbacks.ChainedFuture;
import com.jive.qa.dreidel.api.messages.postgres.PostgresRequestMessage;

/**
 * the visitor that visits MessageCategoryVisitables
 * 
 * @author jdavidson
 *
 * @param <Reply>
 *          the pojo reply needed for the callback
 * @param <Context>
 *          the type of context passed into the accept
 */
public interface MessageCategoryVisitor<Reply, Context>
{
  /**
   * routes all PostgresMessages
   * 
   * @param message
   *          the postgres message that should be visited
   * @param context
   *          the context to be given to the visitor
   * @return a chained future that accepts with reply and fails with a throwable
   */
  ChainedFuture<Reply> visit(final PostgresRequestMessage message, final Context context);
}

package com.jive.qa.dreidel.api.interfaces;

import com.jive.myco.commons.concurrent.PnkyPromise;
import com.jive.qa.dreidel.api.messages.VisitorContext;
import com.jive.qa.dreidel.api.messages.jinst.JinstRequestMessage;
import com.jive.qa.dreidel.api.messages.postgres.PostgresRequestMessage;
import com.jive.qa.dreidel.api.messages.wiremock.WiremockRequestMessage;

/**
 * MessageCategoryVisitor is a visitor that visits MessageCategoryVisitables.
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
   *
   * @param message
   *          the postgres message that should be visited
   * @param context
   *          the context to be given to the visitor
   * @return a PnkyPromise that accepts with reply and fails with a throwable
   */
  PnkyPromise<Reply> visit(final PostgresRequestMessage message, final Context context);

  /**
   *
   * @param message
   *          the jinst message that should be visited
   * @param context
   *          the context to be given to the visitor
   * @return a PnkyPromise that accepts with reply and fails with a throwable
   */
  PnkyPromise<Reply> visit(JinstRequestMessage message, VisitorContext context);

  /**
   *
   * @param message
   *          the wiremock message that should be visited
   * @param context
   *          the context to be given to the visitor
   * @return a PnkyPromise that accepts with reply and fails with a throwable
   */
  PnkyPromise<Reply> visit(WiremockRequestMessage message, VisitorContext context);
}

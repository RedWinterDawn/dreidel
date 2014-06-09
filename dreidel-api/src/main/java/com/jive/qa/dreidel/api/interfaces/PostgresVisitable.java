package com.jive.qa.dreidel.api.interfaces;

import com.jive.myco.commons.concurrent.PnkyPromise;

/**
 * PostgresVisitable is an object that is visitable by a PostgresVisitor.
 * 
 * @author jdavidson
 *
 * @param <Reply>
 *          the pojo reply needed for the callback
 * @param <Context>
 *          the type of context passed into the accept
 */
public interface PostgresVisitable<Reply, Context>
{
  /**
   * accepts a PostgresVisitor
   * 
   * @param visitor
   *          the visitor to visit the message
   * @param context
   *          the context to be passed to the visitor
   * @return a PnkyPromise that will succeed with a reply or fail with a throwable
   */
  PnkyPromise<Reply> accept(final PostgresVisitor<Reply, Context> visitor, final Context context);
}

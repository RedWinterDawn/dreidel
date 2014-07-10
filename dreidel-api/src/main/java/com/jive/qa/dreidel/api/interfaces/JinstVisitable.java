package com.jive.qa.dreidel.api.interfaces;

import com.jive.myco.commons.concurrent.PnkyPromise;

public interface JinstVisitable<Reply, Context>
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
  PnkyPromise<Reply> accept(final JinstVisitor<Reply, Context> visitor, final Context context);
}

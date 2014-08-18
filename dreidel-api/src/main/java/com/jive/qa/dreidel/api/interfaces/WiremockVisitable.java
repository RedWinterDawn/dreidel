package com.jive.qa.dreidel.api.interfaces;

import com.jive.myco.commons.concurrent.PnkyPromise;

public interface WiremockVisitable<Reply, Context>
{
  /**
   * accepts a WiremockVisitor
   *
   * @param visitor
   *          the visitor to visit the message
   * @param context
   *          the context to be passed to the visitor
   * @return a PnkyPromise that will succeed with a reply or fail with a throwable
   */
  PnkyPromise<Reply> accept(final WiremockVisitor<Reply, Context> visitor, final Context context);
}

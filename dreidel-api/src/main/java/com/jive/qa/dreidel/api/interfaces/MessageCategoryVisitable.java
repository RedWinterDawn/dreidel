package com.jive.qa.dreidel.api.interfaces;

import com.jive.myco.commons.callbacks.ChainedFuture;

/**
 * a Message that is visitable by a MessageCategoryVisitor
 * 
 * @author jdavidson
 *
 * @param <Reply>
 *          the pojo reply needed for the callback
 * @param <Context>
 *          the type of context passed into the accept
 */
public interface MessageCategoryVisitable<Reply, Context>
{
  /**
   * accepts a MessageCategoryVisitor
   * 
   * @param visitor
   *          the visitor to visit the message
   * @param context
   *          the context to be passed to the visitor
   * @return a chained future that will succeed with a reply or fail with a throwable
   */
  ChainedFuture<Reply> accept(final MessageCategoryVisitor<Reply, Context> visitor,
      final Context context);
}

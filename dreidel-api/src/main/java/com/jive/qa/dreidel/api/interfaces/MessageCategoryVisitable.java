package com.jive.qa.dreidel.api.interfaces;

import com.jive.myco.commons.callbacks.ChainedFuture;

public interface MessageCategoryVisitable<Reply, Context>
{
  ChainedFuture<Reply> accept(final MessageCategoryVisitor<Reply, Context> visitor,
      final Context context);
}

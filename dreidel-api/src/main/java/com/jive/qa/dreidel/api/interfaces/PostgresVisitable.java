package com.jive.qa.dreidel.api.interfaces;

import com.jive.myco.commons.callbacks.ChainedFuture;

public interface PostgresVisitable<Reply, Context>
{
  ChainedFuture<Reply> accept(final PostgresVisitor<Reply, Context> visitor, final Context context);
}

package com.jive.qa.dreidel.api.interfaces;

import com.jive.myco.commons.callbacks.ChainedFuture;
import com.jive.qa.dreidel.api.messages.postgres.PostgresRequestMessage;

public interface MessageCategoryVisitor<Reply, Context>
{
  ChainedFuture<Reply> visit(final PostgresRequestMessage message, final Context context);
}

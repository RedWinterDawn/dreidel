package com.jive.qa.dreidel.api.interfaces;

import com.jive.myco.commons.callbacks.ChainedFuture;
import com.jive.qa.dreidel.api.messages.postgres.PostgresCreateMessage;
import com.jive.qa.dreidel.api.messages.postgres.PostgresExecSqlMessage;
import com.jive.qa.dreidel.api.messages.postgres.PostgresRestoreMessage;

public interface PostgresVisitor<Reply, Context>
{

  ChainedFuture<Reply> visit(final PostgresCreateMessage message, final Context context);

  ChainedFuture<Reply> visit(final PostgresExecSqlMessage message, final Context context);

  ChainedFuture<Reply> visit(final PostgresRestoreMessage message, final Context context);
}

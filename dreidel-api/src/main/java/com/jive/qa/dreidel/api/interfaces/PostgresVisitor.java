package com.jive.qa.dreidel.api.interfaces;

import com.jive.myco.commons.callbacks.ChainedFuture;
import com.jive.qa.dreidel.api.messages.postgres.PostgresCreateMessage;
import com.jive.qa.dreidel.api.messages.postgres.PostgresExecSqlMessage;
import com.jive.qa.dreidel.api.messages.postgres.PostgresRestoreMessage;

/**
 * the visitor that visits PostgresMessages
 * 
 * @author jdavidson
 *
 * @param <Reply>
 *          the pojo reply needed for the callback
 * @param <Context>
 *          the type of context passed into the accept
 */
public interface PostgresVisitor<Reply, Context>
{

  /**
   * uses a PostgresCreateMessage and a Context to create a database
   * 
   * @param message
   *          the message used to create the database
   * @param context
   *          the context used to associate the database with a user
   * @return a chained future that will succeed with a reply or fail with a throwable
   */
  ChainedFuture<Reply> visit(final PostgresCreateMessage message, final Context context);

  /**
   * uses a PostgresExecSqlMessage and a Context to execute a SQL script against a database found in
   * the PostgresExecSqlMessage
   * 
   * @param message
   *          the message that contains the database id and the SQL to execute against the database
   * @param context
   *          the context used to associate the database with a user
   * @return a chained future that will succeed with a reply or fail with a throwable
   */
  ChainedFuture<Reply> visit(final PostgresExecSqlMessage message, final Context context);

  /**
   * uses a PostgresRestoreMessage and a Context to execute a pg_dump file against the database
   * 
   * @param message
   *          the message that contains the database id and the pg_dump to execute against the
   *          database
   * @param context
   *          the context used to associate the database with the user
   * @return a chained future that will succeed with a reply or fail with a throwable
   */
  ChainedFuture<Reply> visit(final PostgresRestoreMessage message, final Context context);
}

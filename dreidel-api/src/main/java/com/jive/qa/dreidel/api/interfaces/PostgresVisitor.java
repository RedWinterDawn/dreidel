package com.jive.qa.dreidel.api.interfaces;

import com.jive.myco.commons.concurrent.PnkyPromise;
import com.jive.qa.dreidel.api.messages.postgres.PostgresCreateMessage;
import com.jive.qa.dreidel.api.messages.postgres.PostgresExecSqlMessage;
import com.jive.qa.dreidel.api.messages.postgres.PostgresRestoreMessage;

/**
 * PostgresVisitor is a visitor that visits PostgresMessages
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
   * 
   * @param message
   *          the message used to create the database
   * @param context
   *          the context used to associate the database with a user
   * @return a PnkyPromise that will succeed with a reply or fail with a throwable
   */
  PnkyPromise<Reply> visit(final PostgresCreateMessage message, final Context context);

  /**
   * 
   * @param message
   *          the message that contains the database id and the SQL to execute against the database
   * @param context
   *          the context used to associate the database with a user
   * @return a PnkyPromise that will succeed with a reply or fail with a throwable
   */
  PnkyPromise<Reply> visit(final PostgresExecSqlMessage message, final Context context);

  /**
   * 
   * @param message
   *          the message that contains the database id and the pg_dump to execute against the
   *          database
   * @param context
   *          the context used to associate the database with the user
   * @return a PnkyPromise that will succeed with a reply or fail with a throwable
   */
  PnkyPromise<Reply> visit(final PostgresRestoreMessage message, final Context context);
}

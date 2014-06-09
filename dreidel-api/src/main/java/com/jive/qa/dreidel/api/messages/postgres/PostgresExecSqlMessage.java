package com.jive.qa.dreidel.api.messages.postgres;

import java.beans.ConstructorProperties;

import javax.annotation.Nonnull;

import lombok.Getter;
import lombok.NonNull;

import com.jive.myco.commons.concurrent.PnkyPromise;
import com.jive.qa.dreidel.api.interfaces.PostgresVisitor;
import com.jive.qa.dreidel.api.messages.ResourceId;
import com.jive.qa.dreidel.api.messages.VisitorContext;
import com.jive.qa.dreidel.api.replies.Reply;

/**
 * used to execute SQL scripts against a postgres database
 * 
 * @author jdavidson
 *
 */
@Getter
public final class PostgresExecSqlMessage extends PostgresRequestMessage
{

  private final String sql;
  private final ResourceId databaseId;

  @ConstructorProperties({ "referenceId", "databaseId", "sql" })
  public PostgresExecSqlMessage(@NonNull final String referenceId,
      @NonNull final ResourceId databaseId,
      @NonNull final String sql)
  {
    super(referenceId);
    this.sql = sql;
    this.databaseId = databaseId;
  }

  @Override
  public PnkyPromise<Reply> accept(
      final PostgresVisitor<Reply, VisitorContext> visitor,
      final VisitorContext context)
  {

    return visitor.visit(this, context);
  }

}

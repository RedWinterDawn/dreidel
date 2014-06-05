package com.jive.qa.dreidel.api.messages.postgres;

import java.beans.ConstructorProperties;

import lombok.Getter;

import com.jive.myco.commons.callbacks.ChainedFuture;
import com.jive.qa.dreidel.api.interfaces.PostgresVisitor;
import com.jive.qa.dreidel.api.messages.VisitorContext;
import com.jive.qa.dreidel.api.replies.Reply;

@Getter
public class PostgresExecSqlMessage extends PostgresRequestMessage
{

  private final String sql;
  private final String id;

  @ConstructorProperties({ "referenceId", "sql", "id" })
  public PostgresExecSqlMessage(final String referenceId, final String sql, final String id)
  {
    super(referenceId);
    this.sql = sql;
    this.id = id;
  }

  @Override
  public ChainedFuture<Reply> accept(
      final PostgresVisitor<Reply, VisitorContext> visitor,
      final VisitorContext context)
  {

    return visitor.visit(this, context);
  }

}

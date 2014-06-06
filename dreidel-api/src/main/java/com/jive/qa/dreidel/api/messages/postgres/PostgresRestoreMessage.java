package com.jive.qa.dreidel.api.messages.postgres;

import java.beans.ConstructorProperties;

import lombok.Getter;

import com.jive.myco.commons.callbacks.ChainedFuture;
import com.jive.qa.dreidel.api.interfaces.PostgresVisitor;
import com.jive.qa.dreidel.api.messages.VisitorContext;
import com.jive.qa.dreidel.api.replies.Reply;

/**
 * used to restore a pg_dump to postgres database
 * 
 * @author jdavidson
 *
 */
@Getter
public class PostgresRestoreMessage extends PostgresRequestMessage
{
  private final String id;
  private final String pgdump;

  @ConstructorProperties({ "referenceId", "id", "pgDump" })
  public PostgresRestoreMessage(final String referenceId, final String id, final String pgDump)
  {
    super(referenceId);
    this.id = id;
    pgdump = pgDump;
  }

  @Override
  public ChainedFuture<Reply> accept(final PostgresVisitor<Reply, VisitorContext> visitor,
      final VisitorContext context)
  {
    return visitor.visit(this, context);
  }

}

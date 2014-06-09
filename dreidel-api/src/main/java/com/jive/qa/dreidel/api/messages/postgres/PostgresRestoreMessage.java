package com.jive.qa.dreidel.api.messages.postgres;

import java.beans.ConstructorProperties;

import lombok.Getter;
import lombok.NonNull;

import com.jive.myco.commons.concurrent.PnkyPromise;
import com.jive.qa.dreidel.api.interfaces.PostgresVisitor;
import com.jive.qa.dreidel.api.messages.ResourceId;
import com.jive.qa.dreidel.api.messages.VisitorContext;
import com.jive.qa.dreidel.api.replies.Reply;

/**
 * used to restore a pg_dump to postgres database
 * 
 * @author jdavidson
 *
 */
@Getter
public final class PostgresRestoreMessage extends PostgresRequestMessage
{
  private final ResourceId databaseId;
  private final String pgdump;

  @ConstructorProperties({ "referenceId", "databaseId", "pgDump" })
  public PostgresRestoreMessage(@NonNull final String referenceId,
      @NonNull final ResourceId databaseId,
      @NonNull final String pgDump)
  {
    super(referenceId);
    this.databaseId = databaseId;
    pgdump = pgDump;
  }

  @Override
  public PnkyPromise<Reply> accept(final PostgresVisitor<Reply, VisitorContext> visitor,
      final VisitorContext context)
  {
    return visitor.visit(this, context);
  }

}

package com.jive.qa.dreidel.api.messages.postgres;

import java.beans.ConstructorProperties;

import lombok.NonNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jive.myco.commons.concurrent.PnkyPromise;
import com.jive.qa.dreidel.api.interfaces.PostgresVisitor;
import com.jive.qa.dreidel.api.messages.VisitorContext;
import com.jive.qa.dreidel.api.replies.Reply;

/**
 * used to create a postgres database
 * 
 * @author jdavidson
 *
 */
public final class PostgresCreateMessage extends PostgresRequestMessage
{

  @ConstructorProperties({ "referenceId" })
  public PostgresCreateMessage(@NonNull final String referenceId)
  {
    super(referenceId);
  }

  @Override
  @JsonIgnore
  public PnkyPromise<Reply> accept(
      final PostgresVisitor<Reply, VisitorContext> visitor,
      final VisitorContext context)
  {
    return visitor.visit(this, context);
  }

}

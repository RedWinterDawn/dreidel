package com.jive.qa.dreidel.api.messages.jinst;

import java.beans.ConstructorProperties;

import com.jive.myco.commons.concurrent.PnkyPromise;
import com.jive.qa.dreidel.api.interfaces.JinstVisitable;
import com.jive.qa.dreidel.api.interfaces.MessageCategoryVisitor;
import com.jive.qa.dreidel.api.messages.RequestMessage;
import com.jive.qa.dreidel.api.messages.VisitorContext;
import com.jive.qa.dreidel.api.replies.Reply;

public abstract class JinstRequestMessage extends RequestMessage implements
    JinstVisitable<Reply, VisitorContext>
{

  @ConstructorProperties({ "referenceId" })
  public JinstRequestMessage(String referenceId)
  {
    super(referenceId);
  }

  @Override
  public PnkyPromise<Reply> accept(MessageCategoryVisitor<Reply, VisitorContext> visitor,
      VisitorContext context)
  {
    return visitor.visit(this, context);
  }

}

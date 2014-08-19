package com.jive.qa.dreidel.api.messages.wiremock;

import com.jive.myco.commons.concurrent.PnkyPromise;
import com.jive.qa.dreidel.api.interfaces.MessageCategoryVisitor;
import com.jive.qa.dreidel.api.interfaces.WiremockVisitable;
import com.jive.qa.dreidel.api.messages.RequestMessage;
import com.jive.qa.dreidel.api.messages.VisitorContext;
import com.jive.qa.dreidel.api.replies.Reply;

public abstract class WiremockRequestMessage extends RequestMessage implements
    WiremockVisitable<Reply, VisitorContext>
{

  public WiremockRequestMessage(String referenceId)
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

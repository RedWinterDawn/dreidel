package com.jive.qa.dreidel.api.messages.wiremock;

import java.beans.ConstructorProperties;

import lombok.Getter;

import com.jive.myco.commons.concurrent.PnkyPromise;
import com.jive.qa.dreidel.api.interfaces.WiremockVisitor;
import com.jive.qa.dreidel.api.messages.VisitorContext;
import com.jive.qa.dreidel.api.replies.Reply;

@Getter
public class WiremockCreateMessage extends WiremockRequestMessage
{

  @ConstructorProperties({ "referenceId" })
  public WiremockCreateMessage(String referenceId)
  {
    super(referenceId);
  }

  @Override
  public PnkyPromise<Reply> accept(WiremockVisitor<Reply, VisitorContext> visitor,
      VisitorContext context)
  {
    return visitor.visit(this, context);
  }

}

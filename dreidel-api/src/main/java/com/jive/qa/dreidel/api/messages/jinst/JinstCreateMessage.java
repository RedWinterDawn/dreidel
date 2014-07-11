package com.jive.qa.dreidel.api.messages.jinst;

import java.beans.ConstructorProperties;

import lombok.Getter;

import com.jive.myco.commons.concurrent.PnkyPromise;
import com.jive.qa.dreidel.api.interfaces.JinstVisitor;
import com.jive.qa.dreidel.api.messages.VisitorContext;
import com.jive.qa.dreidel.api.replies.Reply;

@Getter
public class JinstCreateMessage extends JinstRequestMessage
{

  private final String jClass;

  @ConstructorProperties({ "referenceId", "jClass" })
  public JinstCreateMessage(String referenceId, String jClass)
  {
    super(referenceId);
    this.jClass = jClass;
  }

  @Override
  public PnkyPromise<Reply> accept(JinstVisitor<Reply, VisitorContext> visitor,
      VisitorContext context)
  {
    return visitor.visit(this, context);
  }

}

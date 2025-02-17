package com.jive.qa.dreidel.api.messages.jinst;

import java.beans.ConstructorProperties;

import lombok.Getter;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jive.myco.commons.concurrent.PnkyPromise;
import com.jive.qa.dreidel.api.interfaces.JinstVisitor;
import com.jive.qa.dreidel.api.messages.VisitorContext;
import com.jive.qa.dreidel.api.replies.Reply;

public class JinstCreateMessage extends JinstRequestMessage
{

  private final String jClass;
  @Getter
  private final String branch;

  // We have this nonsesnse instead of @getter because jackson is having a hard time retrieving this
  // as "jClass" instead of jclass
  @JsonProperty("jClass")
  public String getJClass()
  {
    return jClass;
  }

  @ConstructorProperties({ "referenceId", "jClass", "branch" })
  public JinstCreateMessage(String referenceId, String jClass, String branch)
  {
    super(referenceId);
    this.jClass = jClass;
    this.branch = branch;
  }

  @Override
  public PnkyPromise<Reply> accept(JinstVisitor<Reply, VisitorContext> visitor,
      VisitorContext context)
  {
    return visitor.visit(this, context);
  }

}

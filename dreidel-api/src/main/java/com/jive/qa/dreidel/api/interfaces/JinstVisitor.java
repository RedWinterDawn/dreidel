package com.jive.qa.dreidel.api.interfaces;

import com.jive.myco.commons.concurrent.PnkyPromise;
import com.jive.qa.dreidel.api.messages.jinst.JinstCreateMessage;

public interface JinstVisitor<Reply, Context>
{

  PnkyPromise<Reply> visit(JinstCreateMessage message, Context context);

}

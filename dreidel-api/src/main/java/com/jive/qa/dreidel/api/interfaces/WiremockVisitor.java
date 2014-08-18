package com.jive.qa.dreidel.api.interfaces;

import com.jive.myco.commons.concurrent.PnkyPromise;
import com.jive.qa.dreidel.api.messages.wiremock.WiremockCreateMessage;

public interface WiremockVisitor<Reply, Context>
{

  PnkyPromise<Reply> visit(WiremockCreateMessage message, Context context);

}

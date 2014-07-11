package com.jive.qa.dreidel.rabbi.views;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.google.inject.Inject;
import com.jive.myco.commons.callbacks.Callback;
import com.jive.myco.commons.concurrent.Pnky;
import com.jive.myco.jivewire.api.highlevel.HighLevelTransportConnection;
import com.jive.myco.jivewire.api.highlevel.HighLevelTransportConnectionListener;
import com.jive.qa.dreidel.api.interfaces.MessageCategoryVisitor;
import com.jive.qa.dreidel.api.messages.ConnectionInformationMessage;
import com.jive.qa.dreidel.api.messages.ExceptionMessage;
import com.jive.qa.dreidel.api.messages.Message;
import com.jive.qa.dreidel.api.messages.RequestMessage;
import com.jive.qa.dreidel.api.messages.SuccessMessage;
import com.jive.qa.dreidel.api.messages.VisitorContext;
import com.jive.qa.dreidel.api.replies.ConnectionInformationReply;
import com.jive.qa.dreidel.api.replies.Reply;

/**
 * The connection listener that routes messages to the appropriate visitors
 *
 * @author jdavidson
 *
 */
@Slf4j
@AllArgsConstructor(onConstructor = @__(@Inject))
public final class DreidelTransportConnectionListener implements
    HighLevelTransportConnectionListener<Message, Message>
{

  private final MessageCategoryVisitor<Reply, VisitorContext> routingVisitor;

  @Override
  public void handleEvent(final IncomingEventContext<Message, Message> context)
  {
    log.debug("Unexpected Event {}", context.getMessage());
  }

  @Override
  public void handleRequest(final IncomingRequestContext<Message, Message> context)
  {
    VisitorContext vContext = new VisitorContext(context.getConnection().getId());
    RequestMessage message = (RequestMessage) context.getMessage();

    message
        .accept(routingVisitor, vContext)
        .thenCompose((result) ->
        {
          // TODO change to use the visitor pattern to use a message writing visitor.
            final Pnky<Void> future = Pnky.create();

            if (result instanceof ConnectionInformationReply)
            {
              ConnectionInformationMessage msg =
                  new ConnectionInformationMessage(message.getReferenceId(),
                      ((ConnectionInformationReply) result).getConnections());
              context.writeReply(msg, new LoggingReplyCallback(message.getReferenceId()));
            }
            else
            {
              context.writeReply(new SuccessMessage(message.getReferenceId()),
                  new LoggingReplyCallback(message.getReferenceId()));
            }
            future.resolve(null);
            return future;

          }).onFailure((cause) ->
        {
          log.error("something went wrong", cause);
          context.writeReply(
              new ExceptionMessage(cause, message.getReferenceId()),
              new LoggingReplyCallback(message.getReferenceId()));
        });
  }

  @Override
  public void onClosed(final HighLevelTransportConnection<Message, Message> connection)
  {
    // TODO close connection and clean up resources.
  }

  /**
   * Callback that does nothing on success and logs the error on failure.
   *
   * @author jdavidson
   *
   */
  @AllArgsConstructor
  private class LoggingReplyCallback implements Callback<Void>
  {
    private final String referenceId;

    @Override
    public void onSuccess(final Void result)
    {
      // NO-OP
    }

    @Override
    public void onFailure(final Throwable cause)
    {
      log.error("There was an error replying to message with referenceId {}", referenceId, cause);
    }
  }

}

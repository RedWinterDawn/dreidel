package com.jive.qa.dreidel.api.messages;

import java.beans.ConstructorProperties;
import java.util.List;

import lombok.Getter;

import com.jive.qa.dreidel.api.replies.ConnectionInformation;

/**
 * The message used to send generic connection information
 * 
 * @author jdavidson
 *
 */
@Getter
public class ConnectionInformationMessage extends ReplyMessage
{

  private final List<ConnectionInformation> connections;

  @ConstructorProperties({ "referenceId", "connections" })
  public ConnectionInformationMessage(final String referenceId,
      final List<ConnectionInformation> connections)
  {
    super(referenceId);
    this.connections = connections;
  }

}

package com.jive.qa.dreidel.api.replies;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * a reply that should be used internally. This gets maped to a ConnectionInformationMessage
 * 
 * @author jdavidson
 *
 */
@AllArgsConstructor
@Getter
public class ConnectionInformationReply extends Reply
{
  private final List<ConnectionInformation> connections;
}

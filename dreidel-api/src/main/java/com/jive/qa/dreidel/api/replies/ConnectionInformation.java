package com.jive.qa.dreidel.api.replies;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * the connection information to show how to connect to any type of service
 * 
 * @author jdavidson
 *
 */
@AllArgsConstructor
@Getter
public class ConnectionInformation
{
  private final String type;
  private final String id;
  private final String host;
  private final int port;
  private final Credential credential;
}

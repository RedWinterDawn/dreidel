package com.jive.qa.dreidel.api.replies;

import lombok.AllArgsConstructor;
import lombok.Getter;

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

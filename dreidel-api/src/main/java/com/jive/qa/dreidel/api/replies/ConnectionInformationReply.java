package com.jive.qa.dreidel.api.replies;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ConnectionInformationReply extends Reply
{
  private final List<ConnectionInformation> connections;
}

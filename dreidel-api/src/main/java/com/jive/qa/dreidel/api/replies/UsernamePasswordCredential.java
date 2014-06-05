package com.jive.qa.dreidel.api.replies;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class UsernamePasswordCredential implements Credential
{
  private final String username;
  private final String password;
  private final String type = "usernameAndPassword";
}

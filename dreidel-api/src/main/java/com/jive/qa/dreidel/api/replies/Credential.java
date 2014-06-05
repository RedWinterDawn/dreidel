package com.jive.qa.dreidel.api.replies;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(as = UsernamePasswordCredential.class)
public interface Credential
{
  String getType();
}

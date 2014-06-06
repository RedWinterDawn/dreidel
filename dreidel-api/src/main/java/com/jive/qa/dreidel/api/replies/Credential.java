package com.jive.qa.dreidel.api.replies;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * Generic credential currently only deserializes as UsernamePasswordCredential
 * 
 * @author jdavidson
 *
 */
@JsonDeserialize(as = UsernamePasswordCredential.class)
public interface Credential
{
  /**
   * gets the type of the credential
   * 
   * @return
   */
  String getType();
}

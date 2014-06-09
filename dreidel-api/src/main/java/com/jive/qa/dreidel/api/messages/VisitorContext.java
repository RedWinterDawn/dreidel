package com.jive.qa.dreidel.api.messages;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * accompanies messages as a context, and contains the id of the user interacting with dreidel
 * 
 * @author jdavidson
 *
 */
@Getter
@AllArgsConstructor
public class VisitorContext
{
  String id;

}

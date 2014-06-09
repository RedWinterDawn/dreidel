package com.jive.qa.dreidel.api.transport;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jive.jackson.ConstructorPropertiesAnnotationIntrospector;

/**
 * object mapper preconfigured to meet the needs of dreidel
 * 
 * @author jdavidson
 *
 */
public class DreidelObjectMapper extends ObjectMapper
{

  private static final long serialVersionUID = 1L;

  public DreidelObjectMapper()
  {
    ConstructorPropertiesAnnotationIntrospector.install(this);
  }

}

package com.jive.qa.dreidel.goyim.exceptions;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class GoyimExceptionMapper implements ExceptionMapper<Exception>
{

  @Override
  public Response toResponse(final Exception exception)
  {

    return null;
  }

}

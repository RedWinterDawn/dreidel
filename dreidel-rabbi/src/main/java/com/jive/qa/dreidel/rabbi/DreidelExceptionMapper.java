package com.jive.qa.dreidel.rabbi;

import java.sql.SQLException;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class DreidelExceptionMapper implements ExceptionMapper<Throwable>
{

  @Override
  public Response toResponse(final Throwable exception)
  {
    if (exception instanceof SQLException)
    {
      return Response.status(Status.INTERNAL_SERVER_ERROR).entity(exception.getMessage()).build();
    }
    else if (exception instanceof DatabaseNotFoundException)
    {
      return Response.status(Status.NOT_FOUND).entity(exception.getMessage()).build();
    }
    else if (exception instanceof RuntimeException)
    {
      return Response.status(Status.BAD_REQUEST).entity(exception.getMessage()).build();
    }
    else if (exception instanceof PGDumpException)
    {
      return Response.status(Status.BAD_REQUEST).entity(exception.getMessage()).build();
    }
    else if (exception instanceof SQLFileLoadException)
    {
      return Response.status(Status.INTERNAL_SERVER_ERROR).entity(exception.getMessage()).build();
    }

    return null;
  }

}

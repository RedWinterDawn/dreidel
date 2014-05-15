package com.jive.qa.dreidel.rabbi.views;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.net.UnknownHostException;
import java.sql.SQLException;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang.RandomStringUtils;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.jive.qa.dreidel.rabbi.DatabaseNotFoundException;
import com.jive.qa.dreidel.rabbi.PGDumpException;
import com.jive.qa.dreidel.rabbi.SQLFileLoadException;
import com.jive.qa.dreidel.rabbi.modelViews.PostgresModelView;
import com.jive.qa.dreidel.rabbi.models.PostgresModel;

/**
 * The PostgresView handles the user interface for the restapi
 *
 * @author jdavidson
 *
 */
@Path("/postgres")
@Slf4j
public class PostgresView
{
  private final String ERROR_RUNNING_SCRIPT =
      "There was an error uploading your script to the database \n ";

  private final String DATABASE_NOT_FOUND;

  PostgresModelView postgresModelView;
  PostgresModel postgresModel;

  /**
   * Constructor
   *
   * @param postgresModelView
   *          This will be used to contain the PostgresModels Given to this constructor. This should
   *          be a single instance that is passed around to multiple views
   * @param postgresModel
   *          This is the PostgresModel that will be put in the PostgresModelView when
   *          CreateDatabase is called
   */
  @Inject
  public PostgresView(final PostgresModelView postgresModelView, final PostgresModel postgresModel,
      @Named("timeToExpire") final String timeToExpire)
  {
    this.postgresModelView = postgresModelView;
    this.postgresModel = postgresModel;
    DATABASE_NOT_FOUND =
        "The database was not found.\nYou must send a keep alive every "
            + timeToExpire + " seconds if you don't want your database to be deleted\n";
  }

  /**
   * This method will call the createDatabase method on the postgresModel given in the constructor
   *
   * @return Returns an error message or the connection information to the database you created.
   * @throws UnknownHostException
   */
  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @SneakyThrows(SQLException.class)
  public PostgresConnectionInformation CreateDatabase() throws UnknownHostException
  {
    postgresModel.createDatabase();

    postgresModelView.addPostgresDB(postgresModel);
    return new PostgresConnectionInformation(postgresModel.getName(), postgresModel.getPort(),
        postgresModel.getUsername(),
        postgresModel.getPassword());
  }

  /**
   * * When you call this function it will call a get on the PostgresModelView and give you the
   * information needed. The PostgresModelView should implement some data structure that makes the
   * database expire after 30 seconds
   *
   * @param name
   * @return returns either an error message or a JSON object containing the databases connection
   *         information.
   */
  @GET
  @Path("/{name}")
  @Produces(MediaType.APPLICATION_JSON)
  @SneakyThrows(DatabaseNotFoundException.class)
  public PostgresConnectionInformation keepDatabaseAlive(@PathParam("name") final String name)
  {
    PostgresModel model = postgresModelView.getPostgresDB(name);
    if (model == null)
    {
      DatabaseNotFoundException ex = new DatabaseNotFoundException(DATABASE_NOT_FOUND);
      log.error("There was an error ", ex);
      throw ex;

    }
    return new PostgresConnectionInformation(model.getName(), model.getPort(), model.getUsername(),
        model.getPassword());
  }

  /**
   * Trys to executes the uploadedFile to the PostgresModel. will respond with "Success" if it
   * worked.
   *
   * @param uploadedFile
   *          The string content of a file that has been uploaded. In order for this to not fail it
   *          must be a postgresql compliant SQL String
   * @param name
   *          This is the name of the database you want to upload the file to. If the database is
   *          not found it will return database not found
   * @return a string response
   */
  @POST
  @Path("/{name}")
  @Consumes(MediaType.TEXT_PLAIN)
  @Produces(MediaType.TEXT_PLAIN)
  @SneakyThrows(DatabaseNotFoundException.class)
  public Response uploadFile(final String uploadedFile, @PathParam("name") final String name)
  {
    Response result;
    PostgresModel model = postgresModelView.getPostgresDB(name);
    if (model == null)
    {
      DatabaseNotFoundException ex = new DatabaseNotFoundException(DATABASE_NOT_FOUND);
      log.error("There was an error ", ex);
      throw ex;
    }
    else
    {
      try
      {
        model.executeScript(uploadedFile);
        result = Response.status(Status.ACCEPTED).entity("Success").build();
      }
      catch (Exception ex)
      {
        String entity = ERROR_RUNNING_SCRIPT + ex.getMessage();
        log.error("", ex);
        ex.printStackTrace();
        result = Response.status(Status.BAD_REQUEST).entity(entity).build();
        postgresModelView.remove(name);
      }
    }
    return result;
  }

  @POST
  @Path("/{name}/restore")
  @Consumes(MediaType.TEXT_PLAIN)
  @Produces(MediaType.TEXT_PLAIN)
  @SneakyThrows(DatabaseNotFoundException.class)
  public Response restoreDump(final String uploadedFile, @PathParam("name") final String name)
      throws Exception
  {
    Response result;
    PostgresModel model = postgresModelView.getPostgresDB(name);

    if (model == null)
    {
      DatabaseNotFoundException ex = new DatabaseNotFoundException(DATABASE_NOT_FOUND);
      log.error("There was an error ", ex);
      throw ex;
    }

    File fileToRestore =
        new File(name + RandomStringUtils.randomAlphabetic(10).toLowerCase() + ".sql");

    try
    {
      if (!fileToRestore.exists())
      {
        fileToRestore.createNewFile();
      }

      FileWriter fw = new FileWriter(fileToRestore.getAbsoluteFile());
      BufferedWriter bw = new BufferedWriter(fw);
      bw.write(uploadedFile);
      bw.close();
    }
    catch (Exception e)
    {
      SQLFileLoadException ex = new SQLFileLoadException("Problem uploading SQL file", e);
      log.error("There was an error ", ex);
      throw ex;
    }

    try
    {
      model.restoreDump(fileToRestore);
    }
    catch (PGDumpException | SQLFileLoadException e)
    {
      // TODO: make sure you notify that the database is nuked
      postgresModelView.remove(name);
      throw e;
    }

    result = Response.status(Status.ACCEPTED).entity("Success").build();

    return result;
  }

  /**
   * Data class holds connection information about a database.
   *
   * @author jdavidson
   *
   */
  @AllArgsConstructor
  public static class PostgresConnectionInformation
  {
    public String databaseName;
    public int port;
    public String username;
    public String password;
  }

}

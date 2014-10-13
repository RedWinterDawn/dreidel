package com.jive.qa.dreidel.rabbi.resources;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import com.google.common.net.HostAndPort;
import com.jive.qa.dreidel.api.exceptions.ResourceDestructionException;
import com.jive.qa.dreidel.api.exceptions.ResourceInitializationException;
import com.jive.qa.dreidel.rabbi.PGDumpException;
import com.jive.qa.dreidel.rabbi.SQLFileLoadException;

/**
 * A model for interacting with a Postgres database on an already running Postgres service.
 *
 * @author jdavidson
 *
 */
@Slf4j
@Getter
@Setter
public class JdbcPostgresResource extends BaseResourceImpl implements PostgresResource
{
  private final String username;
  private final String password;
  private final String logPrefix;

  /**
   * Constructor
   *
   * @param host
   *          The DNS resolvable IP Address of the Postgres service you are interacting with.
   * @param port
   *          The port number of the Postgres service you are interacting with.
   * @param username
   *          The username you want to connect to the Postgres server with.
   * @param password
   *          The password of the username given.
   */
  public JdbcPostgresResource(final HostAndPort hap, final String username,
      final String password)
  {
    super(hap);
    this.username = username;
    this.password = password;
    logPrefix = "[" + getId() + "]";
  }

  @Override
  public void init() throws ResourceInitializationException
  {
    log.info("{} Creating Database {}", logPrefix, getId());
    try
    {

      executeQuery("CREATE DATABASE " + getId() + ";", "postgres");
    }
    catch (SQLException e)
    {
      throw new ResourceInitializationException(
          "There was a problem creating the postgres database", e);
    }
  }

  @Override
  public void destroy() throws ResourceDestructionException
  {
    log.info("{} Dropping Database {}", logPrefix, getId());
    try
    {
      executeQuery("DROP DATABASE " + getId() + ";", "postgres");
    }
    catch (SQLException e)
    {
      throw new ResourceDestructionException("There was a problem droping the postgres database", e);
    }
  }

  @Override
  public void executeQuery(final String script) throws SQLException
  {
    executeQuery(script, getId().toString());
  }

  private void executeQuery(final String script, final String database) throws SQLException
  {
    Connection connection = null;
    log.info("{} Executing Script", logPrefix);
    log.trace(script);
    try
    {
      connection = getConnection(database);
      PreparedStatement preparedStatement = connection.prepareStatement(script);
      preparedStatement.execute();
    }

    finally
    {
      if (connection != null)
      {
        connection.close();
      }
    }
  }

  @Override
  public void restoreDump(final File dump) throws PGDumpException, SQLFileLoadException
  {
    try
    {
      // create a psql process and set file to the file passed in
      Process pg_dumper =
          new ProcessBuilder("/usr/local/bin/psql", "-U", getUsername(), "-h", getHap()
              .getHostText(),
              "-p", Long.toString(getHap().getPort()), "-d",
              getId().toString(), "-f", dump.getPath()).start();

      // read the stdout so that we will make sure it runs.
      BufferedReader input = new BufferedReader
          (new InputStreamReader(pg_dumper.getInputStream()));
      String line;
      String output = "";
      while ((line = input.readLine()) != null)
      {
        log.trace(line);
        output += line + "\n";
      }

      // read the stderr so that we will make sure it runs.
      BufferedReader err = new BufferedReader(new InputStreamReader(pg_dumper.getErrorStream()));
      String errOutput = "";
      while ((line = err.readLine()) != null)
      {
        log.trace(line);
        errOutput += line + "\n";
      }

      // wait for the process to end
      pg_dumper.waitFor();

      // check the exit value make sure it is 0
      // psql is a herp derp and it returns 0 when there is a schema error. It outputs schema errors
      // with the prefix of ERROR: so thats what we check. :(
      if (pg_dumper.exitValue() != 0 || errOutput != "" && errOutput.contains("ERROR:"))
      {
        PGDumpException ex = new PGDumpException(output + "\nError: \n" + errOutput);
        throw ex;
      }

    }
    catch (IOException | InterruptedException e)
    {
      // if there was a problem log it and throw it.
      SQLFileLoadException ex = new SQLFileLoadException("Problem uploading SQL file to server", e);
      throw ex;
    }
  }

  private Connection getConnection(final String database) throws SQLException
  {
    return DriverManager.getConnection("jdbc:postgresql://" + getHap().getHostText() + ":"
        + getHap().getPort() + "/" + database, getUsername(), getPassword());
  }

}

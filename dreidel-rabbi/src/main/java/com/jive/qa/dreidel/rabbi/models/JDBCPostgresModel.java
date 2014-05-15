package com.jive.qa.dreidel.rabbi.models;

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

import com.google.inject.Inject;
import com.google.inject.name.Named;
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
public class JDBCPostgresModel extends BaseModel implements PostgresModel
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
  @Inject
  public JDBCPostgresModel(@Named("postgresHost") final String host,
      @Named("postgresPort") final int port,
      @Named("postgresUsername") final String username,
      @Named("postgresPassword") final String password)
  {
    super(host, port);
    this.username = username;
    this.password = password;
    logPrefix = "[" + getName() + "]";
  }

  @Override
  public void createDatabase() throws SQLException
  {
    Connection con = null;
    try
    {
      log.info("{} Creating Database {}", logPrefix, getName());
      con = GetConnection("postgres");
      con.setCatalog("postgres");
      PreparedStatement preparedStatement =
          con.prepareStatement("CREATE DATABASE " + getName() + ";");
      preparedStatement.execute();
    }

    finally
    {
      if (con != null)
      {
        con.close();
      }
    }
  }

  @Override
  public void dropDatabase() throws SQLException
  {
    Connection connection = null;
    log.info("{} Dropping Database {}", logPrefix, getName());
    try
    {
      connection = GetConnection("postgres");
      connection.setCatalog("postgres");
      PreparedStatement preparedStatement =
          connection.prepareStatement("DROP DATABASE " + getName() + ";");
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
  public void executeScript(final String script) throws SQLException
  {
    Connection connection = null;
    log.info("{} Executing Script", logPrefix);
    log.trace(script);
    try
    {
      connection = GetConnection(getName());
      connection.setCatalog(getName());
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
      Process pg_dumper =
          new ProcessBuilder("/usr/local/bin/psql", "-U", getUsername(), "-h", getHost(),
              "-p", Long.toString(getPort()), "-d",
              getName(), "-f", dump.getPath()).start();

      BufferedReader input = new BufferedReader
          (new InputStreamReader(pg_dumper.getInputStream()));
      String line;
      String output = "";
      while ((line = input.readLine()) != null)
      {
        log.trace(line);
        output += line + "\n";
      }

      BufferedReader err = new BufferedReader(new InputStreamReader(pg_dumper.getErrorStream()));
      String errOutput = "";
      while ((line = err.readLine()) != null)
      {
        log.trace(line);
        errOutput += line + "\n";
      }

      pg_dumper.waitFor();
      if (pg_dumper.exitValue() != 0 || errOutput != "")
      {
        PGDumpException ex = new PGDumpException(output + "\n" + errOutput);
        log.error("There was an error ", ex);
        throw ex;
      }
    }

    catch (IOException | InterruptedException e)
    {
      SQLFileLoadException ex = new SQLFileLoadException("Problem uploading SQL file to server", e);
      log.error("There was an error ", ex);
      throw ex;

    }
    finally
    {
      dump.delete();
    }
  }

  private Connection GetConnection(final String database) throws SQLException
  {
    return DriverManager.getConnection("jdbc:postgresql://" + getHost() + ":" + getPort() + "/"
        + database, getUsername(),
        getPassword());
  }

}

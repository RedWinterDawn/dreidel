package com.jive.qa.dreidel.spinnit.postgres;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.TimeUnit;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import com.google.common.base.Charsets;
import com.google.common.io.CharSource;
import com.google.common.io.Files;
import com.google.common.net.HostAndPort;
import com.jive.qa.dreidel.api.messages.ConnectionInformationMessage;
import com.jive.qa.dreidel.api.messages.ExceptionMessage;
import com.jive.qa.dreidel.api.messages.ReplyMessage;
import com.jive.qa.dreidel.api.messages.ResourceId;
import com.jive.qa.dreidel.api.messages.postgres.PostgresCreateMessage;
import com.jive.qa.dreidel.api.messages.postgres.PostgresExecSqlMessage;
import com.jive.qa.dreidel.api.replies.ConnectionInformation;
import com.jive.qa.dreidel.api.replies.UsernamePasswordCredential;
import com.jive.qa.dreidel.spinnit.api.DreidelConnection;
import com.jive.qa.dreidel.spinnit.api.DreidelConnectionException;
import com.jive.qa.dreidel.spinnit.api.DreidelSpinner;

/**
 *
 * @author jdavidson
 *
 */
@Getter
@Slf4j
public class DreidelPostgres
{
  private final String id;
  private final HostAndPort hap;
  private final String logprefix;
  private String databaseName;
  private int port;
  private String username;
  private String password;
  private DreidelConnection connection;
  private final DreidelSpinner spinner;

  /**
   * Creates the Dreidel Postgres instance.
   *
   * @param id
   *          The logging ID of the Dreidel Postgres instance.
   * @param hap
   *          The host and port of the database instance.
   */
  public DreidelPostgres(String id, HostAndPort hap)
  {
    logprefix = "[" + id + "]";
    this.id = id;
    this.hap = hap;
    this.spinner = new DreidelSpinner(id, hap);
  }

  DreidelPostgres(String id, HostAndPort hap, DreidelSpinner spinner)
  {
    logprefix = "[" + id + "]";
    this.id = id;
    this.hap = hap;
    this.spinner = spinner;
  }

  public String getJdbcUrl()
  {
    return "jdbc:postgresql://" + getHap().getHostText() + ":" + getPort() + "/"
        + getDatabaseName();
  }

  /**
   * Spin up a Postgres database. Blocks until the database is available.
   */
  public void spin() throws DreidelConnectionException
  {
    log.debug("{} Spinning up a postgres database", logprefix);
    connection = spinner.connect();
    if (connection != null)
    {
      log.trace("{} Sending PostgresCreateMessage", logprefix);
      ReplyMessage reply;
      try
      {
        reply = connection.writeRequest(new PostgresCreateMessage(id + "creationMessage"), 5,
            TimeUnit.SECONDS);

        log.trace("{} Recieved reply to postrges create message {}", logprefix, reply);
      }
      catch (Exception e)
      {
        throw new DreidelConnectionException(
            "There was a problem sending and recieving message with the dreidel server", e);
      }
      if (reply instanceof ExceptionMessage)
      {
        throw new DreidelConnectionException(((ExceptionMessage) reply).getExceptionMessage());
      }
      else
      {
        log.trace("{} wiring up Connection Information", logprefix);
        ConnectionInformationMessage information = (ConnectionInformationMessage) reply;
        if (information.getConnections().size() > 0)
        {
          ConnectionInformation postgresConnectionInfo = information.getConnections().get(0);
          this.databaseName = postgresConnectionInfo.getId().toString();
          this.port = postgresConnectionInfo.getPort();
          this.password =
              ((UsernamePasswordCredential) postgresConnectionInfo.getCredential()).getPassword();
          this.username =
              ((UsernamePasswordCredential) postgresConnectionInfo.getCredential()).getUsername();
        }
        else
        {
          throw new DreidelConnectionException("Recieved no connection information from server");
        }
      }
    }
    else
    {
      throw new DreidelConnectionException("Unable to get a connection to the dreidel server");
    }
  }

  /**
   * Executes SQL against the database.
   *
   * @param source
   *          The source of the SQL schema or data. Does not work with pg_dump.
   * @throws SQLException
   *           A SQLException if there is a problem with the SQL.
   */
  public void executeSql(final CharSource source) throws SQLException, DreidelConnectionException
  {
    log.debug("{} Executing sql against database {}", logprefix, getDatabaseName());

    if (connection != null)
    {
      try
      {
        ReplyMessage reply =
            connection.writeRequest(
                new PostgresExecSqlMessage(this.id + "Exec Sql message", ResourceId
                    .valueOf(getDatabaseName()), source
                    .read()), 30, TimeUnit.SECONDS);
        if (reply instanceof ExceptionMessage)
        {
          throw new SQLException(((ExceptionMessage) reply).getExceptionMessage());
        }
      }
      catch (IOException ex)
      {
        throw new RemoteSqlExecutionException(ex);
      }
    }
    else
    {
      throw new DreidelConnectionException(
          "You must spin up the database in order to execute sql against it");
    }
  }

  /**
   * Executes a directory containing *.sql files against the database.
   *
   * @param directory
   *          The directory containing the SQL files (schema or data).
   * @throws SQLException
   *           A SQLException if there is a problem with the SQL.
   */
  public void executeSqlDirectory(final File directory) throws SQLException,
      DreidelConnectionException
  {
    executeSqlFiles(directory.listFiles());
  }

  public void executeSqlFiles(final File... files) throws SQLException,
      DreidelConnectionException
  {

    for (File fileEntry : files)
    {
      if (!fileEntry.isDirectory())
      {
        if (fileEntry.getName().endsWith(".sql"))
        {
          CharSource cs = Files.asCharSource(fileEntry, Charsets.UTF_8);
          try
          {
            executeSql(cs);
          }
          catch (SQLException e)
          {
            throw new SQLException("There was a problem uploading the file " + fileEntry.getName()
                + " see cause for details", e);
          }
        }
      }
    }
  }

  public void executeFlyWayDirectory(final File directory) throws SQLException,
      DreidelConnectionException
  {
    File[] listOfFiles = directory.listFiles();

    Arrays.sort(listOfFiles, new Comparator<File>()
    {
      @Override
      public int compare(File o1, File o2)
      {
        String va = o1.getName().split("V|(__.*)")[1];
        String vb = o2.getName().split("V|(__.*)")[1];

        va = normalize(va);
        vb = normalize(vb);

        String[] vera = va.split("\\.");
        String[] verb = vb.split("\\.");

        int i = 0;

        while (i < vera.length && i < verb.length && vera[i].equals(verb[i]))
        {
          i++;
        }

        if (i < vera.length && i < verb.length)
        {
          int diff = Integer.valueOf(vera[i]).compareTo(Integer.valueOf(verb[i]));
          return Integer.signum(diff);
        }
        else
        {
          return Integer.signum(vera.length - verb.length);
        }

      }

      private String normalize(String s)
      {
        return s.replace("_", ".");
      }
    });

    executeSqlFiles(listOfFiles);
  }

}

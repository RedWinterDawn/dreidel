package com.jive.qa.dreidel.spinnit.postgres;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import com.google.common.base.Charsets;
import com.google.common.io.CharSource;
import com.google.common.io.Files;
import com.google.common.net.HostAndPort;
import com.jive.myco.commons.callbacks.CallbackFuture;
import com.jive.qa.dreidel.api.messages.ConnectionInformationMessage;
import com.jive.qa.dreidel.api.messages.ExceptionMessage;
import com.jive.qa.dreidel.api.messages.Message;
import com.jive.qa.dreidel.api.messages.ReplyMessage;
import com.jive.qa.dreidel.api.messages.postgres.PostgresCreateMessage;
import com.jive.qa.dreidel.api.messages.postgres.PostgresExecSqlMessage;
import com.jive.qa.dreidel.api.replies.ConnectionInformation;
import com.jive.qa.dreidel.api.replies.UsernamePasswordCredential;

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

  public void spin() throws DreidelConnectionException
  {
    log.trace("Spinning up a postgres database");
    connection = spinner.connect();
    if (connection != null)
    {
      CallbackFuture<Message> callback = new CallbackFuture<>();
      connection.writeRequest(new PostgresCreateMessage(id + "creationMessage"), 5,
          TimeUnit.SECONDS,
          callback);
      ReplyMessage reply;
      try
      {
        reply = (ReplyMessage) callback.get();
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
        ConnectionInformationMessage information = (ConnectionInformationMessage) reply;
        if (information.getConnections().size() > 0)
        {
          ConnectionInformation postgresConnectionInfo = information.getConnections().get(0);
          this.databaseName = postgresConnectionInfo.getId();
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

  public void executeSql(final CharSource source) throws SQLException, DreidelConnectionException
  {
    log.trace("Executing sql against database {}", this.getDatabaseName());

    if (connection != null)
    {
      try
      {
        CallbackFuture<Message> callback = new CallbackFuture<>();
        connection.writeRequest(
            new PostgresExecSqlMessage(this.id + "Exec Sql message", source.read(), this
                .getDatabaseName()), 30, TimeUnit.SECONDS, callback);
        ReplyMessage reply = (ReplyMessage) callback.get();
        if (reply instanceof ExceptionMessage)
        {
          throw new SQLException(((ExceptionMessage) reply).getExceptionMessage());
        }

      }
      catch (IOException ex)
      {
        throw new RemoteSqlExecutionException(ex);
      }
      catch (InterruptedException | ExecutionException e)
      {
        throw new DreidelConnectionException(
            "You must spin up the database in order to execute sql against it");
      }
    }
    else
    {
      throw new DreidelConnectionException(
          "You must spin up the database in order to execute sql against it");
    }
  }

  public void executeSqlDirectory(final File directory) throws SQLException,
      DreidelConnectionException
  {
    for (File fileEntry : directory.listFiles())
    {
      if (!fileEntry.isDirectory())
      {
        if (fileEntry.getName().endsWith(".sql"))
        {
          CharSource cs = Files.asCharSource(fileEntry, Charsets.UTF_8);
          executeSql(cs);
        }
      }
    }
  }
}

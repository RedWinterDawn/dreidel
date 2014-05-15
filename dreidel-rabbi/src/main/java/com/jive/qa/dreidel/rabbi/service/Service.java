package com.jive.qa.dreidel.rabbi.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import lombok.extern.slf4j.Slf4j;

import org.postgresql.ds.PGSimpleDataSource;

import com.google.inject.Injector;
import com.jive.commons.rest.WebLauncher;

/**
 * An example service class demonstrating injection and the use of annotations to control lifecycle.
 * 
 * @author David Valeri
 */
@Slf4j
public class Service
{

  private final PostgresConfiguration postgresConfiguration;
  private final Injector injector;

  @Inject
  public Service(final PostgresConfiguration postgresConfiguration, final Injector injector)
  {
    this.postgresConfiguration = postgresConfiguration;
    this.injector = injector;
  }

  @PostConstruct
  public void start()
  {
    ClearOldDatabases(postgresConfiguration.getHap().getHostText(), postgresConfiguration.getHap()
        .getPort(), postgresConfiguration.getUsername(), postgresConfiguration.getPassword());

    WebLauncher launcher = new WebLauncher(8020);
    launcher.setBaseInjector(injector);
    launcher.run();
  }

  @PreDestroy
  public void stop()
  {
    log.info("Shutting down.");
  }

  public void ClearOldDatabases(final String host, final int port, final String userName,
      final String password)
  {
    Connection connection = null;

    try
    {
      connection =
          DriverManager.getConnection("jdbc:postgresql://" + host + ":" + port + "/postgres",
              userName, password);

      PreparedStatement command = connection.prepareStatement("Select datname from pg_database");

      ResultSet resultSet = command.executeQuery();

      while (resultSet.next())
      {
        if (!resultSet.getString(1).contains("postgres")
            && !resultSet.getString(1).contains("template"))
        {
          Connection con = null;
          try
          {
            PGSimpleDataSource ds = new PGSimpleDataSource();
            ds.setUser("postgres");
            ds.setDatabaseName("postgres");
            con = ds.getConnection();
            con.prepareStatement("drop database " + resultSet.getString(1)).execute();
            System.out.println("Dropping database " + resultSet.getString(1));
          }
          finally
          {
            if (con != null)
            {
              con.close();
            }
          }
        }
      }
    }
    catch (Exception e)
    {
      System.out.println("there was an error" + e.getMessage());
    }
    finally
    {
      if (connection != null)
      {
        try
        {
          connection.close();
        }
        catch (Exception e)
        {
          System.out.println("There was a problem shutting down the connection " + e.getMessage());
        }
      }
    }
  }

}

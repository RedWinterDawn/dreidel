package com.jive.qa.dreidel.spinnit.postgres;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Set;
import java.util.logging.Logger;

import javax.sql.DataSource;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.Synchronized;

import org.postgresql.ds.PGSimpleDataSource;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.io.CharSource;
import com.google.common.net.HostAndPort;

/**
 * wraps a normal database to lazy load a dreidel postgres database
 * 
 * @author jdavidson
 *
 */
@RequiredArgsConstructor
public class DreidelPGDataSource implements DataSource
{

  private final String name;
  private final HostAndPort dreidelServer;
  private DataSource delegate;
  private Set<CharSource> sqlSources = Sets.newLinkedHashSet();

  /**
   * add a sql script to be loaded when the connection is opened
   * 
   * @param sql
   *          the sql to execute at load time
   * @param additional
   *          var args to add multiple in a single function call
   * @return returns itself so this function can be chained
   */
  public DreidelPGDataSource addSqlSource(CharSource sql, CharSource... additional)
  {
    this.sqlSources.add(sql);
    if (additional != null && additional.length > 0)
    {
      this.sqlSources.addAll(Lists.newArrayList(additional));
    }
    return this;
  }

  @Override
  public PrintWriter getLogWriter() throws SQLException
  {
    return this.delegate().getLogWriter();
  }

  @Override
  public void setLogWriter(PrintWriter out) throws SQLException
  {
    this.delegate().setLogWriter(out);
  }

  @Override
  public void setLoginTimeout(int seconds) throws SQLException
  {
    this.delegate().setLoginTimeout(seconds);
  }

  @Override
  public int getLoginTimeout() throws SQLException
  {
    return this.delegate().getLoginTimeout();
  }

  @Override
  public Logger getParentLogger() throws SQLFeatureNotSupportedException
  {
    return this.delegate().getParentLogger();
  }

  @Override
  public <T> T unwrap(Class<T> iface) throws SQLException
  {
    return this.delegate().unwrap(iface);
  }

  @Override
  public boolean isWrapperFor(Class<?> iface) throws SQLException
  {
    return this.delegate().isWrapperFor(iface);
  }

  @Override
  public Connection getConnection() throws SQLException
  {
    return this.delegate().getConnection();
  }

  @Override
  public Connection getConnection(String username, String password) throws SQLException
  {
    return this.delegate().getConnection(username, password);
  }

  @Synchronized
  @SneakyThrows
  private DataSource delegate()
  {
    if (this.delegate == null)
    {
      DreidelPostgres connection = new DreidelPostgres(name, dreidelServer);
      connection.spin();
      PGSimpleDataSource ds = new PGSimpleDataSource();
      ds.setServerName(dreidelServer.getHostText());
      ds.setPortNumber(connection.getPort());
      ds.setDatabaseName(connection.getDatabaseName());
      ds.setUser(connection.getUsername());
      ds.setPassword(connection.getPassword());
      for (CharSource source : sqlSources)
      {
        connection.executeSql(source);
      }
      this.delegate = ds;
    }
    return this.delegate;
  }

}

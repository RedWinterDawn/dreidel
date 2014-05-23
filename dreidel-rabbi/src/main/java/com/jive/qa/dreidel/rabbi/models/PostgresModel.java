package com.jive.qa.dreidel.rabbi.models;

import java.io.File;
import java.sql.SQLException;

import com.jive.qa.dreidel.rabbi.PGDumpException;
import com.jive.qa.dreidel.rabbi.SQLFileLoadException;

public interface PostgresModel extends BaseModelInterface
{
  /**
   * Calls DROP DATABASE on the database name you gave this object in the constructor.
   */
  public void dropDatabase() throws SQLException;

  /**
   * Calls CREATE DATABASE on the database name you gave this object in the constructor.
   */
  public void createDatabase() throws SQLException;

  /**
   * Executes the script given against the database. This can be any Postgres SQL compliant script.
   * If there are no exceptions when running the script it will return true. Otherwise it will print
   * the stack trace and return false.
   *
   * @param script
   *          The SQL script you wanted to execute.
   */
  public void executeQuery(final String script) throws SQLException;

  public String getUsername();

  public String getPassword();

  /**
   * Restores a pg_dump file to the database.
   * 
   * @param dump
   *          The location of the file to be dumped.
   * @return true if it worked.
   * @throws Exception
   */
  void restoreDump(final File dump) throws PGDumpException, SQLFileLoadException;
}

package com.jive.qa.dreidel.rabbi.resources;

import java.io.File;
import java.sql.SQLException;

import com.jive.qa.dreidel.rabbi.PGDumpException;
import com.jive.qa.dreidel.rabbi.SQLFileLoadException;

public interface PostgresResource extends BaseResource
{

  /**
   * Executes the script given against the database. This can be any Postgres SQL compliant script.
   * If there are no exceptions when running the script it will return true. Otherwise it will print
   * the stack trace and return false.
   *
   * @param script
   *          The SQL script you wanted to execute.
   */
  public void executeQuery(final String script) throws SQLException;

  /**
   * Restores a pg_dump file to the database.
   * 
   * @param dump
   *          The location of the file to be dumped.
   * @return true if it worked.
   * @throws Exception
   */
  void restoreDump(final File dump) throws PGDumpException, SQLFileLoadException;

  /**
   * @return the username of the postgres database
   */
  public String getUsername();

  /**
   * @return the username of the postgres database
   */
  public String getPassword();
}

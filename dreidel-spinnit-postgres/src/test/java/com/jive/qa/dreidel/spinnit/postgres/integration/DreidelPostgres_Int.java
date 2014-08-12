package com.jive.qa.dreidel.spinnit.postgres.integration;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;

import com.google.common.base.Charsets;
import com.google.common.io.CharSource;
import com.google.common.io.Resources;
import com.google.common.net.HostAndPort;
import com.jive.qa.dreidel.spinnit.postgres.DreidelPostgres;

public class DreidelPostgres_Int
{

  @Test
  public void TestAllTheThings() throws Exception
  {
    final CharSource source = Resources.asCharSource(Resources.getResource("test.sql"), Charsets.UTF_8);

    final HostAndPort server = HostAndPort.fromParts("10.20.27.84", 8020);
    final DreidelPostgres db = new DreidelPostgres("test-db", server); // Create the database
    db.spin();
    assertNotNull(db.getDatabaseName());

    db.executeSql(source); // upload the schema
    System.out.println("Ready to upload data...");
    db.executeSql(source); // submit the schema again to make sure the database is available.
    System.out.println("---Our test is done---");
  }

  @Test
  public void TestAllTheThingsDir() throws Exception
  {
    final HostAndPort server = HostAndPort.fromParts("10.20.27.84", 8020);
    final DreidelPostgres db = new DreidelPostgres("test-db", server); // Create the database
    db.spin();
    assertNotNull(db.getDatabaseName());

    db.executeSqlDirectory(new File("./src/test/resources")); // upload the directory
    System.out.println("---Our test is done---");
  }

}

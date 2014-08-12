package com.jive.qa.dreidel.spinnit.postgres.integration;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.ResultSet;

import javax.sql.DataSource;

import org.junit.Test;
import org.skife.jdbi.v2.DBI;

import com.google.common.base.Charsets;
import com.google.common.io.CharSource;
import com.google.common.io.Resources;
import com.google.common.net.HostAndPort;
import com.jive.qa.dreidel.spinnit.postgres.DreidelPGDataSource;

public class DreidelPGDataSource_Int
{

  @Test
  public void TestAllTheThings() throws Exception
  {
    final CharSource source = Resources.asCharSource(Resources.getResource("test.sql"), Charsets.UTF_8);
    final HostAndPort server = HostAndPort.fromParts("localhost", 8020);
    final DataSource ds = new DreidelPGDataSource("test-db", server).addSqlSource(source);
    final Connection c = ds.getConnection();
    final DBI jdbi = new DBI(ds);
    jdbi.open().insert("insert into stuff values(?, ?);", 1, "Hello World!");

    final ResultSet r = c.createStatement().executeQuery("select * from stuff;");
    while (r.next())
    {
      assertEquals(1, r.getInt("id"));
      assertEquals("Hello World!", r.getString("value"));
    }
    c.close();
  }

}

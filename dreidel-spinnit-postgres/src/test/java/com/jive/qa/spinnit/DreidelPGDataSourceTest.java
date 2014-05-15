package com.jive.qa.spinnit;

import static org.junit.Assert.assertEquals;

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

public class DreidelPGDataSourceTest
{

  @Test
  public void TestAllTheThings() throws Exception
  {
    CharSource source = Resources.asCharSource(Resources.getResource("test.sql"), Charsets.UTF_8);
    HostAndPort server = HostAndPort.fromParts("dreidel.ftw.jiveip.net", 8020);
    DataSource ds = new DreidelPGDataSource("test-db", server).addSqlSource(source);
    Connection c = ds.getConnection();
    DBI jdbi = new DBI(ds);
    jdbi.open().insert("insert into stuff values(?, ?);", 1, "Hello World!");
    
    ResultSet r = c.createStatement().executeQuery("select * from stuff;");
    while (r.next())
    {
      assertEquals(1, r.getInt("id"));
      assertEquals("Hello World!", r.getString("value"));
    }
    c.close();
  }

}

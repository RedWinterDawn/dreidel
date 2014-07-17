com.jive.qa.spinnit
===================

A library to interface smoothly with dreidel

#Usage
Add this to your maven pom.xml

```JAVA
<dependency>
  <groupId>com.jive.qa.dreidel</groupId>
  <artifactId>dreidel-spinnit-postgres</artifactId>
  <version>0.0.2</version>
  <scope>test</scope>
</dependency>
```


Run the following code

```JAVA
HostAndPort server = HostAndPort.fromParts("dreidel.ftw.jiveip.net", 8020);
DreidelPostgres db = new DreidelPostgres("test-db", server);  // Create the database
db.spin();

CharSource source = Resources.asCharSource(Resources.getResource("test.sql"), Charsets.UTF_8);
db.executeSql(source);
```
After this you can use the db object to get information to wire up your database like host, port, password, and username.

There is a thread that will do a get at /postgres/<dbname> every 10 seconds to keep the database alive while you test.  
This is a deamon thread so when your tests are done that daemon will go away and your database will be released.

You can also use the ```DreidelPGDataSource``` to create a lazy loading data source that will spin up a database for you when you connect to the datasource.
```JAVA
    CharSource source = Resources.asCharSource(Resources.getResource("test.sql"), Charsets.UTF_8);
    HostAndPort server = HostAndPort.fromParts("dreidel.ftw.jiveip.net", 8020);
    DataSource ds = new DreidelPGDataSource("test-db", server).addSqlSource(source);
    Connection c = ds.getConnection();
    c.close();
```

#Tests

For both of these examples you should put them in your @Before so that you can have a consistent state for your database for every test.

Happy Testing!

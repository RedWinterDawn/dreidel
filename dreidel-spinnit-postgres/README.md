com.jive.qa.spinnit
===================

A library to interface smoothly with dreidel

Usage
===
Add this to your maven pom.xml

```
<dependency>
  <groupId>com.jive.qa.dreidel</groupId>
  <artifactId>dreidel-spinnit-postgres</artifactId>
  <version>0.0.1</version>
  <scope>test</scope>
</dependency>
```


Run the following code

```
@Before
public void Test() {
    HostAndPort server = HostAndPort.fromParts("10.103.1.34", 8020);
    DreidelPostgres db = new DreidelPostgres("test-db", server);  // Create the database
    db.spin();

    CharSource source = Resources.asCharSource(Resources.getResource("test.sql"), Charsets.UTF_8);
    db.executeSql(source);
}
```

There is a thread that will do a get at /postgres/<dbname> every 10 seconds to keep the database alive while you test.  This is a deamon thread so when your tests are done that daemon will go away and your database will be released.

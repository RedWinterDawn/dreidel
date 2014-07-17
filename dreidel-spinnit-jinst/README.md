#Dreidel-Spinnit-Jinst
dreidel-spinnit-jinst is a client library to allow you to spin up simple jinst classes (ones without any external dependencies) in the qa hardware during unit tests so you can test your stuff that depends on said service

#Example

```JAVA
@Before
  public void setup() throws DreidelConnectionException, InterruptedException, UnknownHostException,
      IOException
  {
    DreidelJinst jinst =
        new DreidelJinst("testing123", HostAndPort.fromParts("10.20.27.84", 8020),
            "dreidel-test123");

	int timeoutInMinutes = 3
    jinst.spin(timeoutInMinutes);

    log.debug("checking to see if the ip address {} is reachable", jinst.getHost());
    
    assertTrue(InetAddress.getByName(jinst.getHost()).isReachable(10000));

  }
```

Here's the pom you need to include

```XML
<dependency>
  <groupId>com.jive.qa.dreidel</groupId>
  <artifactId>dreidel-spinnit-jinst</artifactId>
  <version>0.1.1</version>
  <scope>test</scope>
</dependency>
```

#NOTES

Currently the QA hardware is on a QA vlan so you will need to add this to the list of networks you check (vmcontrolorm is still valid)

Currently you will have to hard code this ip address.  In the future this will have a DNS entry / post to jumpy (or whatever the service registry happens to be).

###Happy Testing!

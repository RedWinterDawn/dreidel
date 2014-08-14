#Dreidel-Spinnit-Jinst
dreidel-spinnit-jinst is a client library to allow you to spin up jinst classes including ones with dependencies in the qa hardware during unit tests so you can test your stuff that depends on said service.


To wire up two services to talk to each other you should do the following

+ Spin up both services
+ Modify the first service so it's properties contain the host of the second service
+ Modify the second service so it's properties contain the host of the first service
+ Run your test




#Example
This is an example where a service and its dependency are started up and the service is pointed at it's dependency

```JAVA
@Before
  public void setup() throws Exception
  {
    HostAndPort dreidelServer = HostAndPort.fromParts("localhost", 8020);

    DreidelJinst jinstService =
        DreidelJinstBuilder.builder().id("service").hap(dreidelServer).jinstClass("dreidel-goyim")
            .workspace("US5057-workspaces").build();
    DreidelJinst jinstDependency = new DreidelJinst("dependency", dreidelServer, "boneyard");

    // (PnkyPromises are futures)
    PnkyPromise<Void> servicePromise = jinstService.spin(4);
    PnkyPromise<Void> dependencyPromise = jinstDependency.spin(4);

    servicePromise.get();
    dependencyPromise.get();

    // This map contains all of the property key values you wish to have in the properties file
    // it will overwrite existing properties, leave ones you don't specify alone, and add new
    // properties to the file.
    Map<String, String> serviceProperties = Maps.newHashMap();
    serviceProperties.put("dependencyIp", jinstDependency.getHost());

    jinstService.setPropertiesAndRestart(serviceProperties,
        "/etc/jive/boneyard/service.properties",
        "boneyard");

    assertTrue(jinstService.getServiceStatus("boneyard"));

    log.debug("checking to see if the ip address {} is reachable", jinstService.getHost());
    assertTrue(InetAddress.getByName(jinstService.getHost()).isReachable(10000));

    log.debug("checking to see if the ip address {} is reachable", jinstDependency.getHost());
    assertTrue(InetAddress.getByName(jinstDependency.getHost()).isReachable(10000));
  }
```

Here's the pom you need to include

```XML
<dependency>
  <groupId>com.jive.qa.dreidel</groupId>
  <artifactId>dreidel-spinnit-jinst</artifactId>
  <version>0.1.4</version>
  <scope>test</scope>
</dependency>
```

#NOTES

Currently the QA hardware is on a QA vlan so you will need to add this to the list of networks you check (vmcontrolorm is still valid)

Currently you will have to hard code this ip address.  In the future this will have a DNS entry / post to jumpy (or whatever the service registry happens to be).

###Happy Testing!

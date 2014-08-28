#Dreidel-Spinnit-Wiremock

wiremock is a tool used to mock rest services.  dreidel-spinnit-wiremock allows you to spin up wiremock instances quickly and easily keep context between wiremock instances.

#POM.xml
```XML
<dependency>
	<groupId>com.jive.qa.dreidel</groupId>
	<artifactId>dreidel-spinnit-wiremock</artifactId>
	<version>0.1.8</version>
	<scope>test</scope>
</dependency>
```

#Example code

make sure to do a static import so you can get the `get()` and other such methods

```JAVA
import static com.github.tomakehurst.wiremock.client.WireMock.*;
```


```JAVA
	DreidelWiremock wiremock =
        new DreidelWiremock("fake hmac", HostAndPort.fromParts("10.20.26.251", 8020));

    WiremockConfigurator configurator = wiremock.spin();

    log.debug("connecting on port {}", configurator.getHap().getPort());

	//returns a 404 Content-Type application/json with a json body when someone calls a GET at the "/my/resource" url
    configurator.stubFor(get(urlEqualTo("/my/resource")).willReturn(
        aResponse()
            .withStatus(404)
            .withHeader("Content-Type", "appplication/json")
            .withBody("{\"json\" : \"testing\"}")));
```

Wiremock api is static and it is tricky to use with multiple instances so the `stubFor()`, `verify()` and other static methods have been ecapsulated into a WiremockConfigurator object that you can use.  All other static methods like `get()` and `urlEqualTo()` that don't relate to a perticular instance have been unchanged.  Please reference the [wiremock documentation](http://wiremock.org/) for more advanced features like selective proxying and rest call verification.

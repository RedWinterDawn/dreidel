package com.jive.qa.dreidel.spinnit.wiremock;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

import java.util.concurrent.ExecutionException;

import lombok.extern.slf4j.Slf4j;

import org.junit.Test;

import com.google.common.net.HostAndPort;
import com.jive.qa.dreidel.spinnit.api.DreidelConnectionException;

@Slf4j
public class DreidelWiremock_Int
{

  @Test
  public void test() throws DreidelConnectionException, InterruptedException, ExecutionException
  {

    DreidelWiremock wiremock =
        new DreidelWiremock("fake hmac", HostAndPort.fromParts("localhost", 8020));

    WiremockConfigurator configurator = wiremock.spin();

    log.debug("connecting on port {}", configurator.getHap().getPort());

    configurator.stubFor(get(urlEqualTo("/my/resource")).willReturn(
        aResponse()
            .withStatus(404)
            .withHeader("Content-Type", "appplication/json")
            .withBody("{\"json\" : \"testing\"}")));

    while (true)
    {
    }

  }
}

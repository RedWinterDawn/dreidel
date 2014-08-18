package com.jive.qa.dreidel.spinnit.wiremock;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

import java.util.concurrent.ExecutionException;

import lombok.extern.slf4j.Slf4j;

import org.junit.Test;

import com.google.common.net.HostAndPort;
import com.jive.qa.dreidel.spinnit.api.DreidelConnectionException;

@Slf4j
public class DreidelWiremockTest
{

  @Test
  public void test() throws DreidelConnectionException, InterruptedException, ExecutionException
  {

    DreidelWiremock wiremock =
        new DreidelWiremock("fake hmac", HostAndPort.fromParts("localhost", 8020));

    wiremock.spin();

    configureFor(wiremock.getHost(), wiremock.getPort());

    log.debug("connecting on port {}", wiremock.getPort());

    stubFor(get(urlEqualTo("/my/resource")).willReturn(
        aResponse()
            .withStatus(404)
            .withHeader("Content-Type", "appplication/json")
            .withBody("{\"json\" : \"testing\"}")));

    while (true)
    {
    }

  }
}

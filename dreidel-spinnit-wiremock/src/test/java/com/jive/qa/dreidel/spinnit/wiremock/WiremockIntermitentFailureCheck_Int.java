package com.jive.qa.dreidel.spinnit.wiremock;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.Assert.*;
import lombok.extern.slf4j.Slf4j;

import org.junit.Test;

import com.google.common.net.HostAndPort;
import com.jive.qa.dreidel.spinnit.api.DreidelConnectionException;

@Slf4j
public class WiremockIntermitentFailureCheck_Int
{

  @Test
  public void test() throws DreidelConnectionException, InterruptedException
  {
    for (int i = 0; i < 50; i++)
    {
      DreidelWiremock wiremock =
          new DreidelWiremock("testing " + i, HostAndPort.fromParts("10.20.26.251", 8020));

      WiremockConfigurator configurator = wiremock.spin();

      try
      {
        configurator.stubFor(get(urlMatching(".*")).willReturn(aResponse().withStatus(200)));
      }
      catch (Exception e)
      {
        log.error("there was a problem connecting {}", i, e);
        log.info("tring again..... {}", i);

        fail();
      }

    }
  }

}

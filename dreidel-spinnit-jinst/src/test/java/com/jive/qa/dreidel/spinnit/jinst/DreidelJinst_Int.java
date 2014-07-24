package com.jive.qa.dreidel.spinnit.jinst;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import lombok.extern.slf4j.Slf4j;

import org.junit.Test;

import com.google.common.collect.Maps;
import com.google.common.net.HostAndPort;
import com.jive.qa.dreidel.spinnit.api.DreidelConnectionException;

@Slf4j
public class DreidelJinst_Int
{
  @Test
  public void test() throws DreidelConnectionException, InterruptedException, UnknownHostException,
      IOException, ExecutionException
  {
    DreidelJinst jinst =
        new DreidelJinst("testing123", HostAndPort.fromParts("10.20.27.84", 8020),
            "dreidel-test123");

    jinst.spin(3);

    Thread.sleep(5000);

    Map<String, String> properties = Maps.newHashMap();

    properties.put("foo", "bar");
    properties.put("buzz", "bang");

    jinst.setProperties(properties, "/etc/jive/boneyard/service.properties", "boneyard");

    log.debug("checking to see if the ip address {} is reachable", jinst.getHost());
    assertTrue(InetAddress.getByName(jinst.getHost()).isReachable(10000));

    while (true)
    {

    }

  }
}

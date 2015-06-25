package com.jive.qa.dreidel.spinnit.jinst;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import lombok.extern.slf4j.Slf4j;

import org.junit.Ignore;
import org.junit.Test;

import com.google.common.collect.Maps;
import com.google.common.net.HostAndPort;
import com.jive.myco.commons.concurrent.PnkyPromise;
import com.jive.qa.dreidel.spinnit.api.DreidelConnectionException;
import com.jive.qa.dreidel.spinnit.jinst.DreidelJinst.DreidelJinstBuilder;

@Slf4j
@Ignore
public class DreidelJinst_Int
{
  @Test
  public void test() throws DreidelConnectionException, InterruptedException, UnknownHostException,
      IOException, ExecutionException
  {
    HostAndPort dreidelServer = HostAndPort.fromParts("10.20.27.84", 8020);

    DreidelJinst jinstService =
        DreidelJinstBuilder.builder().id("service").hap(dreidelServer).jinstClass("dreidel-goyim")
            .build();
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
}

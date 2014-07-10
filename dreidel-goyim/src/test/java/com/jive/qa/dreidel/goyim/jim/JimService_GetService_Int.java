package com.jive.qa.dreidel.goyim.jim;

import java.net.MalformedURLException;
import java.net.URL;

import lombok.extern.slf4j.Slf4j;

import org.junit.Test;

import com.jive.qa.dreidel.goyim.exceptions.ServiceNotFoundException;
import com.jive.qa.dreidel.goyim.models.ServiceDetail;

@Slf4j
public class JimService_GetService_Int extends TestEndpoint
{

  @Test
  public void test() throws MalformedURLException, ServiceNotFoundException
  {

    JimService jimService =
        new JimService(jimEndpoint, new URL("http://jim.devops.jive.com:8080/"));
    ServiceDetail detail = jimService.getService("dreidel-test123");
    log.debug("here is the service name {}", detail.getName());
    log.debug("something happened {}", detail);
  }
}

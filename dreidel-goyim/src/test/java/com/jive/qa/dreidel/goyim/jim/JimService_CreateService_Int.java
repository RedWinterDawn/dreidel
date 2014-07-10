package com.jive.qa.dreidel.goyim.jim;

import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Test;

import com.google.common.collect.Lists;
import com.jive.qa.dreidel.goyim.exceptions.JimCreationException;
import com.jive.qa.dreidel.goyim.models.Disk;
import com.jive.qa.dreidel.goyim.models.ServiceDetail;

public class JimService_CreateService_Int extends TestEndpoint
{

  @Test
  public void test() throws MalformedURLException, JimCreationException
  {
    JimService jimService =
        new JimService(jimEndpoint, new URL("http://jim.devops.jive.com:8080/"));

    jimService.createService(new ServiceDetail("testing", 512, 2, Lists.newArrayList("base"), Lists
        .newArrayList(new Disk("root", "4G")), Lists.newArrayList()));
  }

}

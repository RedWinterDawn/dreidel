package com.jive.qa.dreidel.goyim.jim;

import java.util.Map;

import org.junit.Before;

import com.google.common.collect.Maps;
import com.jive.qa.dreidel.goyim.messages.JimMessage;
import com.jive.qa.dreidel.goyim.restinator.DreidelObjectMapper;
import com.jive.qa.dreidel.goyim.restinator.JimCodec;
import com.jive.qa.restinator.Endpoint;

public class TestEndpoint
{
  protected Endpoint<JimMessage, JimMessage> jimEndpoint;

  @Before
  public void setupEndpoint()
  {
    Map<String, String> headers = Maps.newHashMap();
    headers.put("Authorization", "Token  token=715d5af0aaa92acc2ee1d954e9c4686d");
    jimEndpoint =
        new Endpoint<JimMessage, JimMessage>(new JimCodec(new DreidelObjectMapper()), headers);
  }
}

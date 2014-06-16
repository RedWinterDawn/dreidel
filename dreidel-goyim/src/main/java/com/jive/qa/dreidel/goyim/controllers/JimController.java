package com.jive.qa.dreidel.goyim.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.Map;

import javax.inject.Named;

import com.google.common.collect.Maps;
import com.jive.qa.dreidel.goyim.exceptions.JimCreationException;
import com.jive.qa.dreidel.goyim.exceptions.JimDestructionException;
import com.jive.qa.dreidel.goyim.messages.JimErrorMessage;
import com.jive.qa.dreidel.goyim.messages.JimMessage;
import com.jive.qa.dreidel.goyim.messages.JimResponseCodeOnly;
import com.jive.qa.dreidel.goyim.messages.NewInstanceMessage;
import com.jive.qa.dreidel.goyim.models.Instance;
import com.jive.qa.restinator.Endpoint;

public class JimController
{

  private final Endpoint<JimMessage, JimMessage> endpoint;
  private final URL url;

  public JimController(@Named("jimEndpoint") Endpoint<JimMessage, JimMessage> endpoint,
      @Named("jimUrl") URL url)
  {
    this.endpoint = endpoint;
    this.url = url;
  }

  public boolean serviceExists(String service)
  {
    JimMessage serviceMessage;
    try
    {
      serviceMessage = this.endpoint.url(url, "api/services/" + service).get();
    }
    catch (Exception e)
    {
      // TODO throw exception instead of returning false
      return false;
    }

    if (serviceMessage != null)
    {
      return true;
    }
    else
    {
      return false;
    }
  }

  public void createInstance(String service, Instance instance) throws JimCreationException
  {

    Map<String, String> headers = Maps.newHashMap();
    // TODO use constants
    headers.put("Content-Type", "application/json");
    // create instance using Jim
    JimMessage response = null;
    try
    {
      response = endpoint.url(url, "/api/services/" + service + "/instances").post(
          new NewInstanceMessage(instance),
          headers);
    }
    catch (IOException e)
    {
      throw new JimCreationException("There was a problem creating a jimstance", e);
    }
    if (response instanceof JimErrorMessage)
    {
      throw new JimCreationException(((JimErrorMessage) response).getResponseMessage());
    }
    else if (response instanceof JimResponseCodeOnly)
    {
      if (((JimResponseCodeOnly) response).getResponseCode() == 200)
      {
        // TODO actually stand up the instance
      }
      else
      {
        // TODO what if it's not a 200 ok
      }
    }
  }

  public void deleteInstance(String service, int instanceId, String site)
      throws JimDestructionException
  {
    JimMessage response = null;
    try
    {
      response =
          endpoint.url(url,
              "/api/services/" + service + "/instances/" + instanceId + "/?site=" + site)
              .delete();
    }
    catch (Exception e)
    {
      throw new JimDestructionException("There was a problem destroying the jimstance", e);
    }
    if (response instanceof JimErrorMessage)
    {
      throw new JimDestructionException(((JimErrorMessage) response).getResponseMessage());
    }
    else if (response instanceof JimResponseCodeOnly)
    {
      if (((JimResponseCodeOnly) response).getResponseCode() == 200)
      {
        // TODO actually delete the instance on the machine
      }
      else
      {
        // TODO what if it's not a 200 ok
      }
    }

  }
}

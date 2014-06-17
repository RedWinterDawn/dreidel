package com.jive.qa.dreidel.goyim.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.Map;

import javax.inject.Named;

import lombok.extern.slf4j.Slf4j;

import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.jive.qa.dreidel.goyim.exceptions.JimCreationException;
import com.jive.qa.dreidel.goyim.exceptions.JimDestructionException;
import com.jive.qa.dreidel.goyim.messages.JimErrorMessage;
import com.jive.qa.dreidel.goyim.messages.JimInstanceAlreadyExistsMessage;
import com.jive.qa.dreidel.goyim.messages.JimMessage;
import com.jive.qa.dreidel.goyim.messages.JimResponseCodeOnly;
import com.jive.qa.dreidel.goyim.messages.NewInstanceMessage;
import com.jive.qa.dreidel.goyim.models.Instance;
import com.jive.qa.restinator.Endpoint;

@Slf4j
public class JimController
{

  private final Endpoint<JimMessage, JimMessage> endpoint;
  private final URL url;

  @Inject
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
      log.error("There was a problem checking to see if the service exists", e);
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

  public void createInstance(String service, Instance instance) throws JimCreationException,
      JimDestructionException
  {

    Map<String, String> headers = Maps.newHashMap();
    // TODO use constants
    headers.put("Content-Type", "application/json");
    // create instance using Jim
    for (int i = 0; i < 2; i++)
    {
      JimMessage response = null;
      try
      {
        response = endpoint.url(url, "/api/services/" + service + "/instances").post(
            new NewInstanceMessage(instance),
            headers);
      }
      catch (IOException e)
      {
        log.error("There was a problem creating a jinstance", e);
        throw new JimCreationException("There was a problem creating a jimstance", e);
      }
      if (response == null)
      {
        throw new JimCreationException("unknown message recieved from jim");
      }
      else if (response instanceof JimErrorMessage)
      {
        throw new JimCreationException(((JimErrorMessage) response).getResponseMessage());
      }
      else if (response instanceof JimInstanceAlreadyExistsMessage)
      {
        // TODO delete the instance from jim. and continue;
        deleteInstance(service, instance.getInstance(), instance.getSite());
        continue;
      }
      else if (response instanceof JimResponseCodeOnly
          && ((JimResponseCodeOnly) response).getResponseCode() == 200)
      {
        // everything is awesome!
        // TODO actually stand up jimstance
        return;
      }
      else
      {
        throw new JimCreationException("There was an unknown problem creating an instance");
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

  public boolean instanceExists(String service, int id, String site)
  {
    JimMessage instanceMessage;
    try
    {
      instanceMessage =
          this.endpoint.url(url, "api/services/" + service + "/instances/" + id + "/?site=" + site)
              .get();
    }
    catch (Exception e)
    {
      // TODO throw exception instead of returning false
      return false;
    }

    if (instanceMessage != null)
    {
      return true;
    }
    else
    {
      return false;
    }
  }
}

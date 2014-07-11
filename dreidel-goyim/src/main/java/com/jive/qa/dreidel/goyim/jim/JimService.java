package com.jive.qa.dreidel.goyim.jim;

import java.io.IOException;
import java.net.URL;
import java.util.Map;

import javax.inject.Named;

import lombok.extern.slf4j.Slf4j;

import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.jive.qa.dreidel.goyim.exceptions.JimCreationException;
import com.jive.qa.dreidel.goyim.exceptions.JimDestructionException;
import com.jive.qa.dreidel.goyim.exceptions.ServiceNotFoundException;
import com.jive.qa.dreidel.goyim.messages.JimErrorMessage;
import com.jive.qa.dreidel.goyim.messages.JimInstanceAlreadyExistsMessage;
import com.jive.qa.dreidel.goyim.messages.JimMessage;
import com.jive.qa.dreidel.goyim.messages.JimResponseCodeOnly;
import com.jive.qa.dreidel.goyim.messages.NewInstanceMessage;
import com.jive.qa.dreidel.goyim.messages.ServiceDetailMessage;
import com.jive.qa.dreidel.goyim.models.Instance;
import com.jive.qa.dreidel.goyim.models.ServiceDetail;
import com.jive.qa.dreidel.goyim.restinator.JimErrorList;
import com.jive.qa.restinator.Endpoint;

@Slf4j
public class JimService
{

  private final Endpoint<JimMessage, JimMessage> endpoint;
  private final URL url;
  private final Map<String, String> headers;

  @Inject
  public JimService(@Named("jimEndpoint") Endpoint<JimMessage, JimMessage> endpoint,
      @Named("jimUrl") URL url)
  {
    this.endpoint = endpoint;
    this.url = url;
    this.headers = Maps.newHashMap();
    // TODO use constants
    headers.put("Content-Type", "application/json");
  }

  public void createInstance(String service, Instance instance) throws JimCreationException,
      JimDestructionException
  {
    JimMessage response;
    for (int i = 0; i < 2; i++)
    {
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
        deleteInstance(service, instance.getInstance(), instance.getSite());
        continue;
      }
      else if (response instanceof JimResponseCodeOnly
          && ((JimResponseCodeOnly) response).getResponseCode() != 200)
      {
        throw new JimCreationException("Something went wrong and we don't know what");
      }
      // otherwise it worked.
      return;
    }
    throw new JimCreationException(
        "Instance already exists.  Unable to delete it. Notify someone because this is a bug.");
  }

  public void bootInstance(String service, int instance, String site) throws JimCreationException,
      JimDestructionException
  {
    JimMessage response;
    try
    {
      response =
          endpoint.url(url,
              "/api/services/" + service + "/instances/" + instance + "/?site=" + site).put(
              new ServiceDetailMessage(null),
              headers);
    }
    catch (IOException e)
    {
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
    else if (response instanceof JimResponseCodeOnly
        && ((JimResponseCodeOnly) response).getResponseCode() != 200)
    {
      throw new JimCreationException("Something went wrong and we don't know what");
    }
    // otherwise it worked.
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
      if (((JimResponseCodeOnly) response).getResponseCode() != 200)
      {
        // TODO what if it's not a 200 ok
      }
    }
    // everything is awesome! (the instance was deleted)
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

  public ServiceDetail getService(String service) throws ServiceNotFoundException
  {
    ServiceDetail rtn = null;
    JimMessage message;
    try
    {
      message = this.endpoint.url(url, "api/services/" + service).get();
    }
    catch (IOException e)
    {
      throw new ServiceNotFoundException("We were unable to find the jim service " + service, e);
    }
    if (message instanceof ServiceDetailMessage)
    {
      rtn = ((ServiceDetailMessage) message).getService();
    }
    return rtn;
  }

  public void createService(ServiceDetail serviceDetails) throws JimCreationException
  {

    JimMessage message;
    try
    {
      message =
          endpoint.url(url, "api/services/").post(
              new ServiceDetailMessage(serviceDetails), headers);
    }
    catch (IOException e)
    {
      throw new JimCreationException("unable to create the service " + serviceDetails.getName(), e);
    }
    if (!(message instanceof JimResponseCodeOnly)
        || ((JimResponseCodeOnly) message).getResponseCode() != 200)
    {
      if (message instanceof JimErrorList)
      {
        // TODO don't log throw the error some how
        for (String error : ((JimErrorList) message).getErrors())
        {
          log.debug("there was an error {}", error);
        }
      }
      throw new JimCreationException("There was something wrong with creating the message "
          + message);
    }
    // everything is osm! (we created the service)
  }

  public void deleteService(String service) throws JimDestructionException
  {
    JimMessage message;
    try
    {
      message = endpoint.url(url, "api/services/" + service).delete();
    }
    catch (Exception e)
    {
      throw new JimDestructionException("problem deleting the service" + service, e);
    }
    if (message instanceof JimResponseCodeOnly
        && ((JimResponseCodeOnly) message).getResponseCode() != 200)
    {
      throw new JimDestructionException("problem deleting the service" + service
          + " error code recieved from jim " + ((JimResponseCodeOnly) message).getResponseCode());
    }
    // otherwise everything worked
  }
}

package com.jive.qa.dreidel.goyim.jinst.service.rest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/")
public class GoyimView
{
  @POST
  @Path("/properties/{path:.*}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Map<Object, Object> changeProperty(@PathParam("path") String path,
      Map<String, String> properties) throws FileNotFoundException, IOException
  {
    File f = new File("/" + path);
    Properties file = new Properties();
    file.load(new FileInputStream(f));

    for (Entry<Object, Object> entry : file.entrySet())
    {
      String removal = null;
      for (Entry<String, String> newEntry : properties.entrySet())
      {
        if (entry.getKey().equals(newEntry.getKey()))
        {
          entry.setValue(newEntry.getValue());
          removal = newEntry.getKey();
          break;
        }
      }
      if (removal != null)
      {
        properties.remove(removal);
      }
    }

    for (Entry<String, String> entry : properties.entrySet())
    {
      file.put(entry.getKey(), entry.getValue());
    }

    file.store(new FileOutputStream(f), null);

    return file;
  }

  @PUT
  @Path("service/{serviceName}")
  public String restartService(@PathParam("serviceName") String serviceName) throws Exception
  {
    Process serviceRestarter = new ProcessBuilder("service", serviceName, "restart").start();

    serviceRestarter.waitFor();

    if (serviceRestarter.exitValue() == 0)
    {
      return "\"SUCCESS!\"";
    }
    else
    {
      throw new Exception("There was something wrong while trying to restart the service");
    }
  }

  @GET
  @Path("service/{serviceName}")
  public String getServiceStatus(@PathParam("serviceName") String serviceName) throws Exception
  {
    Process serviceStatusGetter = new ProcessBuilder("service", serviceName, "status").start();

    serviceStatusGetter.waitFor();

    BufferedReader input =
        new BufferedReader
        (new InputStreamReader(serviceStatusGetter.getInputStream()));

    StringBuilder builder = new StringBuilder();
    String aux = "";

    while ((aux = input.readLine()) != null)
    {
      builder.append(aux + "\n");
    }

    return "\"" + builder.toString() + "\"";

  }

}

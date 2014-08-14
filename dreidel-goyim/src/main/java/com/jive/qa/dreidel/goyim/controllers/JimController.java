package com.jive.qa.dreidel.goyim.controllers;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import jersey.repackaged.com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.jive.qa.dreidel.goyim.exceptions.JimCreationException;
import com.jive.qa.dreidel.goyim.exceptions.JimDestructionException;
import com.jive.qa.dreidel.goyim.exceptions.ServiceNotFoundException;
import com.jive.qa.dreidel.goyim.models.InstanceDetails;
import com.jive.qa.dreidel.goyim.models.NewInstance;
import com.jive.qa.dreidel.goyim.models.ServiceDetail;
import com.jive.qa.dreidel.goyim.rest.JimService;

@Slf4j
@AllArgsConstructor(onConstructor = @__(@Inject))
public class JimController
{

  private final JimService endpoint;
  private final Map<String, String> instances = Maps.newHashMap();

  public InstanceDetails createInstance(final String service, final String site, final String branch)
      throws JimCreationException,
      JimDestructionException, ServiceNotFoundException, InterruptedException, ExecutionException
  {
    final String serviceId = UUID.randomUUID().toString().replace("-", "").substring(0, 5);
    // TODO try catch finally make sure we don't have lingering dreidel services

    // download service
    final ServiceDetail serviceDetails = endpoint.getService(service).get();
    // muck with service
    serviceDetails.setName(serviceDetails.getName() + "-dreidel-" + serviceId);
    serviceDetails.setCpus(1);
    serviceDetails.setMemory(512);
    serviceDetails.getClasses().add(0, "base");

    serviceDetails.getClasses().add("dreidel-goyim-jinst");
    // TODO we need to do this more inteligently so we don't bork other networks a service may need.
    serviceDetails.getNetworks().clear();
    serviceDetails.getNetworks().add("vmcontrolorm");
    serviceDetails.getNetworks().add("dev");

    // push the service to jim
    try
    {
      endpoint.createService(serviceDetails).get();
    }
    catch (Exception e)
    {
      log.error("there was a problem", e);
    }

    // create instance using Jim
    final NewInstance in = new NewInstance(site, serviceDetails.getName(), branch);

    InstanceDetails details = null;

    try
    {
      details = endpoint.createInstance(in).get(30, TimeUnit.SECONDS);
    }
    catch (Exception e)
    {
      log.error("Something went wrong trying to create the instance", e);
      throw new JimCreationException("Problem creating the instance", e);
    }

    instances.put(details.getRid(), serviceId);

    // boot the instance

    endpoint.bootInstance(details.getRid()).get();

    return details;

  }

  public void deleteInstance(final String service, final String rid)
      throws JimDestructionException, InterruptedException, ExecutionException
  {
    endpoint.deleteInstance(rid).get();

    // delete the service
    // Always delete the service after creating it so it isn't left hanging around and doesn't
    // have to
    // be deleted elsewhere

    String serviceId = instances.get(rid);

    endpoint.deleteService(service + "-dreidel-" + serviceId).get();
  }

  public boolean serviceExists(final String service)
  {
    try
    {
      endpoint.getService(service).get();
      return true;
    }
    catch (final Exception e)
    {
      log.error("service {} seems to not exist", service, e);
      return false;
    }

  }

  public boolean instanceExists(final String rid)
  {
    try
    {
      endpoint.getInstance(rid).get();
      return true;
    }
    catch (final Exception e)
    {
      log.error("instance {} seems to not exist", rid, e);
      return false;
    }

  }
}

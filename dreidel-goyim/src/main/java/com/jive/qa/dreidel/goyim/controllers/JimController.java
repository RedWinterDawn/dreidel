package com.jive.qa.dreidel.goyim.controllers;

import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.inject.Inject;

import lombok.AllArgsConstructor;
import net.logstash.logback.encoder.org.apache.commons.lang.RandomStringUtils;

import com.jive.qa.dreidel.goyim.exceptions.JimCreationException;
import com.jive.qa.dreidel.goyim.exceptions.JimDestructionException;
import com.jive.qa.dreidel.goyim.exceptions.ServiceNotFoundException;
import com.jive.qa.dreidel.goyim.models.InstanceDetails;
import com.jive.qa.dreidel.goyim.models.NewInstance;
import com.jive.qa.dreidel.goyim.models.ServiceDetail;
import com.jive.qa.dreidel.goyim.rest.JimResource;

@AllArgsConstructor(onConstructor = @__(@Inject))
public class JimController
{

  private final JimResource endpoint;
  private final Map<String, InstanceDetails> instances;

  public InstanceDetails createInstance(final String service, final String site)
      throws JimCreationException,
      JimDestructionException, ServiceNotFoundException, InterruptedException, ExecutionException
  {
    final String instanceId = RandomStringUtils.randomAlphanumeric(5);
    // TODO try catch finally make sure we don't have lingering dreidel services

    // download service
    final ServiceDetail serviceDetails = endpoint.getService(service).get();
    // muck with service
    serviceDetails.setName(serviceDetails.getName() + "-dreidel-" + instanceId);
    serviceDetails.setCpus(1);
    serviceDetails.setMemory(512);
    serviceDetails.getClasses().add(0, "base");

    serviceDetails.getClasses().add("dreidel-goyim-jinst");
    // TODO we need to do this more inteligently so we don't bork other networks a service may need.
    serviceDetails.getNetworks().clear();
    serviceDetails.getNetworks().add("vmcontrolorm");
    serviceDetails.getNetworks().add("dev");

    // push the service to jim
    endpoint.createService(serviceDetails).get();

    // create instance using Jim
    final NewInstance in = new NewInstance(site, service, "master");
    final InstanceDetails details = endpoint.createInstance(in).get();

    instances.put(instanceId, details);

    // boot the instance

    endpoint.bootInstance(instanceId).get();

    return details;

  }

  public void deleteInstance(final String service, final String instanceId)
      throws JimDestructionException, InterruptedException, ExecutionException
  {

    // delete the instance
    final InstanceDetails details = instances.get(instanceId);
    endpoint.deleteInstance(details.getRid()).get();

    // delete the service
    // Always delete the service after creating it so it isn't left hanging around and doesn't
    // have to
    // be deleted elsewhere

    endpoint.deleteService(service + "-dreidel-" + instanceId).get();
  }

  public boolean serviceExists(final String service)
  {
    try
    {
      endpoint.getService(service);
      return true;
    }
    catch (final Exception e)
    {
      return false;
    }

  }

  public boolean instanceExists(final String instanceId)
  {
    try
    {
      final InstanceDetails details = instances.get(instanceId);
      endpoint.getInstance(details.getRid());
      return true;
    }
    catch (final Exception e)
    {
      return false;
    }

  }
}

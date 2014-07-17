package com.jive.qa.dreidel.goyim.controllers;

import com.google.inject.Inject;
import com.jive.qa.dreidel.goyim.exceptions.JimCreationException;
import com.jive.qa.dreidel.goyim.exceptions.JimDestructionException;
import com.jive.qa.dreidel.goyim.exceptions.ServiceNotFoundException;
import com.jive.qa.dreidel.goyim.jim.JimService;
import com.jive.qa.dreidel.goyim.models.Instance;
import com.jive.qa.dreidel.goyim.models.ServiceDetail;
import com.jive.qa.dreidel.goyim.service.BmSettings;

public class JimController
{

  private final JimService jimService;
  private final BmSettings bmSettings;

  @Inject
  public JimController(JimService service, BmSettings settings)
  {
    this.jimService = service;
    this.bmSettings = settings;
  }

  public void createInstance(String service, Instance instance) throws JimCreationException,
      JimDestructionException, ServiceNotFoundException
  {
    // TODO try catch finally make sure we dont have lingering dreidel services

    // download service
    ServiceDetail serviceDetails = jimService.getService(service);
    // muck with service
    serviceDetails.setName(serviceDetails.getName() + "-dreidel-" + instance.getInstance());
    serviceDetails.setCpus(1);
    serviceDetails.setMemory(512);
    serviceDetails.getClasses().add(0, "base");

    serviceDetails.getClasses().add("dreidel-goyim-jinst");
    // TODO we need to do this more inteligently so we don't bork other networks a service may need.
    serviceDetails.getNetworks().clear();
    serviceDetails.getNetworks().add("vmcontrolorm");
    serviceDetails.getNetworks().add("qa");

    // push the service to jim
    jimService.createService(serviceDetails);

    // create instance using Jim
    jimService.createInstance(serviceDetails.getName(), instance);

    // boot the instance
    jimService.bootInstance(serviceDetails.getName(), instance.getInstance(), bmSettings.getSite());

  }

  public void deleteInstance(String service, int instanceId)
      throws JimDestructionException
  {

    // delete the instance
    jimService.deleteInstance(service, instanceId, bmSettings.getSite());

    // delete the service
    // Always delete the service after creating it so it isn't left hanging around and doesn't
    // have to
    // be deleted elsewhere
    jimService.deleteService(service + "-dreidel-" + instanceId);
  }

}

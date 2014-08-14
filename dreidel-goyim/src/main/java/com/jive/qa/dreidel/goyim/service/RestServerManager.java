package com.jive.qa.dreidel.goyim.service;

import java.util.Optional;
import java.util.concurrent.ExecutionException;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jaxrs.cfg.Annotations;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import com.google.inject.name.Named;
import com.jive.myco.jazz.api.core.network.Network;
import com.jive.myco.jazz.api.core.network.NetworkId;
import com.jive.myco.jazz.api.rest.RestServiceBinding;
import com.jive.myco.jazz.api.rest.RestServiceDescriptor;
import com.jive.myco.jazz.api.rest.RestServiceManager;
import com.jive.myco.jazz.api.runtime.JazzRuntimeEnvironment;
import com.jive.qa.dreidel.goyim.views.DreidelView;

public class RestServerManager
{

  private final JazzRuntimeEnvironment jazzRuntime;
  private final RestServiceManager restServiceManager;
  private final DreidelView view;
  private final ObjectMapper objectMapper;
  private final int port;

  private RestServiceBinding binding;

  @Inject
  public RestServerManager(final JazzRuntimeEnvironment jazzRuntime,
      final RestServiceManager restServiceManager,
      final DreidelView view, final ObjectMapper objectMapper,
      @Named("rest-server-port") final int port)
  {
    this.jazzRuntime = jazzRuntime;
    this.restServiceManager = restServiceManager;
    this.view = view;
    this.objectMapper = objectMapper;
    this.port = port;
  }

  @PostConstruct
  public void start() throws InterruptedException, ExecutionException
  {
    final Network network =
        Optional
            .ofNullable(
                Optional
                    .ofNullable(
                        jazzRuntime.getNetworks().getNetwork(NetworkId.valueOf("v4compat")))
                    .orElse(
                        jazzRuntime.getNetworks().getNetwork(NetworkId.valueOf("qa"))))
            .orElseGet(() -> jazzRuntime.getNetworks().getNetwork(NetworkId.valueOf("dev")));

    binding =
        restServiceManager
            .addService(
                RestServiceDescriptor
                    .builder()
                    .addSingleton(view)
                    .addSingleton(
                        new JacksonJaxbJsonProvider(objectMapper,
                            new Annotations[] { Annotations.JACKSON }))
                    .build(),
                "/",
                network.getId(),
                null,
                port)
            .get();
  }

  @PreDestroy
  public void stop() throws InterruptedException, ExecutionException
  {
    if (binding != null)
    {
      binding.remove().get();
    }
  }

}

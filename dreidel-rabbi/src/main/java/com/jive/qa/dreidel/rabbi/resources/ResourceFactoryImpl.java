package com.jive.qa.dreidel.rabbi.resources;

import java.net.URL;

import com.google.common.net.HostAndPort;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.jive.qa.dreidel.api.messages.goyim.IdResponse;
import com.jive.qa.dreidel.api.messages.goyim.ResponseCodeOnly;
import com.jive.qa.restinator.Endpoint;

/**
 *
 * @author jdavidson
 *
 */

public class ResourceFactoryImpl implements ResourceFactory
{

  private final URL creatorUrl;
  private final Endpoint<IdResponse, Void> instanceCreator;
  private final Endpoint<ResponseCodeOnly, Void> instanceDeleter;
  private final String wiremockLocation;

  @Inject
  public ResourceFactoryImpl(@Named("creationUrl") URL creatorUrl,
      @Named("creationEndpoint") Endpoint<IdResponse, Void> instanceCreator,
      @Named("deletionEndpoint") Endpoint<ResponseCodeOnly, Void> instanceDeleter,
      @Named("wiremock.jar.location") String wiremockLocation)
  {
    this.creatorUrl = creatorUrl;
    this.instanceCreator = instanceCreator;
    this.instanceDeleter = instanceDeleter;
    this.wiremockLocation = wiremockLocation;
  }

  @Override
  public PostgresResource getPostgresResource(final HostAndPort hap, final String username,
      final String password)
  {
    return new JdbcPostgresResource(hap, username, password);
  }

  @Override
  public JinstResource getJinstResource(String jClass, String branch)
  {
    return new JinstResource(jClass, creatorUrl, instanceCreator, instanceDeleter, branch);
  }

  @Override
  public WiremockResource getWiremockResource(HostAndPort hap)
  {
    return new WiremockResource(hap, wiremockLocation);
  }
}

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

  @Inject
  public ResourceFactoryImpl(@Named("creationUrl") URL creatorUrl,
      @Named("creationEndpoint") Endpoint<IdResponse, Void> instanceCreator,
      @Named("deletionEndpoint") Endpoint<ResponseCodeOnly, Void> instanceDeleter)
  {
    this.creatorUrl = creatorUrl;
    this.instanceCreator = instanceCreator;
    this.instanceDeleter = instanceDeleter;
  }

  @Override
  public PostgresResource getPostgresResource(final HostAndPort hap, final String username,
      final String password)
  {
    return new JdbcPostgresResource(hap, username, password);
  }

  @Override
  public JinstResource getJinstResource(String jClass)
  {
    return new JinstResource(jClass, creatorUrl, instanceCreator, instanceDeleter);
  }
}

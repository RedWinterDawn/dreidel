package com.jive.qa.dreidel.rabbi.resources;

import com.google.common.net.HostAndPort;

public interface ResourceFactory
{
  PostgresResource getPostgresResource(final HostAndPort hap, final String username,
      final String password);

}

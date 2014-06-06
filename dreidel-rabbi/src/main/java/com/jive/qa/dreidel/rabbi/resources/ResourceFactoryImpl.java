package com.jive.qa.dreidel.rabbi.resources;

import com.google.common.net.HostAndPort;

/**
 * 
 * @author jdavidson
 *
 */
public class ResourceFactoryImpl implements ResourceFactory
{

  @Override
  public PostgresResource getPostgresResource(final HostAndPort hap, final String username,
      final String password)
  {
    return new JDBCPostgresResource(hap.getHostText(), hap.getPort(), username, password);
  }
}

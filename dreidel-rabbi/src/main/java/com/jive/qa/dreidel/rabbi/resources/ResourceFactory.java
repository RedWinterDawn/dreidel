package com.jive.qa.dreidel.rabbi.resources;

import com.google.common.net.HostAndPort;

/**
 * the factory that produces the individual resource types.
 * 
 * @author jdavidson
 *
 */
public interface ResourceFactory
{
  /**
   * 
   * @param hap
   *          the host and port to connect to the postgres server
   * @param username
   *          the username to connect to the postgres server
   * @param password
   *          the password to connect to the postgres server
   * @return a postgres resource
   */
  PostgresResource getPostgresResource(final HostAndPort hap, final String username,
      final String password);

}

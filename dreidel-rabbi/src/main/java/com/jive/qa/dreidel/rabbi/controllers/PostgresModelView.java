package com.jive.qa.dreidel.rabbi.controllers;

import com.jive.qa.dreidel.rabbi.resources.PostgresResource;

public interface PostgresModelView
{
  void addPostgresDB(final PostgresResource postgresModel);

  PostgresResource getPostgresDB(final String name);

  void remove(final String name);
}

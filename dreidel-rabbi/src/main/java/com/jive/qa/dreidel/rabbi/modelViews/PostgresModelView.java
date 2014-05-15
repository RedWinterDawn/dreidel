package com.jive.qa.dreidel.rabbi.modelViews;

import com.jive.qa.dreidel.rabbi.models.PostgresModel;

public interface PostgresModelView
{
  void addPostgresDB(final PostgresModel postgresModel);

  PostgresModel getPostgresDB(final String name);

  void remove(final String name);
}

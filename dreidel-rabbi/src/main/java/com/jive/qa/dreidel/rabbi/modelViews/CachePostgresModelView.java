package com.jive.qa.dreidel.rabbi.modelViews;

import com.google.common.cache.Cache;
import com.google.inject.Inject;
import com.jive.qa.dreidel.rabbi.models.PostgresModel;

/**
 * This uses a Google Guava Cache object to store its PostgresModels
 * 
 * @author jdavidson
 *
 */
public class CachePostgresModelView implements PostgresModelView
{

  /**
   * Constructor.
   * 
   * @param cache
   *          The instance of the cache you would like the model to use. Should RemoveOnAccess after
   *          30 seconds. Other options are up to you.
   */
  @Inject
  public CachePostgresModelView(final Cache<String, PostgresModel> cache)
  {
    this.cache = cache;
  }

  Cache<String, PostgresModel> cache;

  /**
   * Gets the PostgresModel with the name given.
   * 
   * @param name
   *          The name of the PostgresModel you want.
   */
  @Override
  public PostgresModel getPostgresDB(final String name)
  {
    return cache.getIfPresent(name);
  }

  /**
   * Adds a PostgresModel to the cache.
   * 
   * @param postgresModel
   *          The PostgresModel you want to add to the cache.
   */
  @Override
  public void addPostgresDB(final PostgresModel postgresModel)
  {
    cache.put(postgresModel.getName(), postgresModel);
  }

  @Override
  public void remove(final String name)
  {
    cache.invalidate(name);
  }
}

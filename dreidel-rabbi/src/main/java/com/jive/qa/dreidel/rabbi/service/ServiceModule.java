package com.jive.qa.dreidel.rabbi.service;

import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Named;
import com.jive.jackson.ConstructorPropertiesAnnotationIntrospector;
import com.jive.qa.dreidel.rabbi.DreidelExceptionMapper;
import com.jive.qa.dreidel.rabbi.NetworkInterfaceInetGetter;
import com.jive.qa.dreidel.rabbi.modelViews.CachePostgresModelView;
import com.jive.qa.dreidel.rabbi.modelViews.PostgresModelView;
import com.jive.qa.dreidel.rabbi.models.JDBCPostgresModel;
import com.jive.qa.dreidel.rabbi.models.PostgresModel;
import com.jive.qa.dreidel.rabbi.views.PostgresView;

/**
 * Example of a module that bootstraps the classes used by the Jazz Service.
 * 
 * @author David Valeri
 */
public class ServiceModule extends AbstractModule
{

  @Override
  protected void configure()
  {
    // Bind the example service class as a singleton.
    bind(Service.class).asEagerSingleton();

    this.bind(PostgresView.class);
    this.bind(PostgresModel.class).to(JDBCPostgresModel.class);
    this.bind(PostgresModelView.class).to(CachePostgresModelView.class);

    this.bind(NetworkInterfaceInetGetter.class).asEagerSingleton();
    this.bind(Runtime.class).toInstance(Runtime.getRuntime());

    this.bind(DreidelExceptionMapper.class).asEagerSingleton();

  }

  @Provides
  public ObjectMapper getMapper()
  {
    ObjectMapper json = new ObjectMapper().registerModule(new JodaModule());
    ConstructorPropertiesAnnotationIntrospector.install(json);
    return json;
  }

  @Provides
  @Singleton
  public JacksonJsonProvider getJsonProvider(final ObjectMapper json)
  {
    return new JacksonJsonProvider(json);
  }

  @Provides
  @Named("timeToExpire")
  public long getTime(@Named("timeToExpire") final String time)
  {
    return Long.valueOf(time);
  }

  @Provides
  @Singleton
  public Cache<String, PostgresModel> getPostgresCache(@Named("timeToExpire") final long time)
  {
    // ModelView cache for Postgres
    Cache<String, PostgresModel> cache = CacheBuilder.newBuilder()
        .expireAfterAccess(time, TimeUnit.SECONDS)
        // call drop database when you remove it so the database wont stay in the Postgres server
        .removalListener(new RemovalListener<String, PostgresModel>()
        {
          @Override
          public void onRemoval(final RemovalNotification<String, PostgresModel> notification)
          {
            PostgresModel model = notification.getValue();
            try
            {
              model.dropDatabase();
            }
            catch (SQLException e)
            {
              e.printStackTrace();
            }
          }

        }).build();

    return cache;
  }

}

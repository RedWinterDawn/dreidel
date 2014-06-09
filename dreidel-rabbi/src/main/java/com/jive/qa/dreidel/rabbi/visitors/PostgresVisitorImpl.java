package com.jive.qa.dreidel.rabbi.visitors;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang.RandomStringUtils;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.jive.myco.commons.concurrent.Pnky;
import com.jive.myco.commons.concurrent.PnkyPromise;
import com.jive.qa.dreidel.api.exceptions.ResourceInitializationException;
import com.jive.qa.dreidel.api.interfaces.PostgresVisitor;
import com.jive.qa.dreidel.api.messages.ResourceId;
import com.jive.qa.dreidel.api.messages.VisitorContext;
import com.jive.qa.dreidel.api.messages.postgres.PostgresCreateMessage;
import com.jive.qa.dreidel.api.messages.postgres.PostgresExecSqlMessage;
import com.jive.qa.dreidel.api.messages.postgres.PostgresRestoreMessage;
import com.jive.qa.dreidel.api.replies.ConnectionInformation;
import com.jive.qa.dreidel.api.replies.ConnectionInformationReply;
import com.jive.qa.dreidel.api.replies.Reply;
import com.jive.qa.dreidel.api.replies.SuccessReply;
import com.jive.qa.dreidel.api.replies.UsernamePasswordCredential;
import com.jive.qa.dreidel.rabbi.resources.BaseResource;
import com.jive.qa.dreidel.rabbi.resources.PostgresResource;
import com.jive.qa.dreidel.rabbi.resources.ResourceFactory;
import com.jive.qa.dreidel.rabbi.service.PostgresConfiguration;

/**
 * visitor to handle postgres messages
 * 
 * @author jdavidson
 *
 */
@AllArgsConstructor(onConstructor = @__(@Inject))
@Slf4j
public class PostgresVisitorImpl implements
    PostgresVisitor<Reply, VisitorContext>
{

  private final PostgresConfiguration configuration;
  private final Map<String, List<BaseResource>> resourceCorrelationMap;
  private final ResourceFactory resourceFactory;

  @Override
  public PnkyPromise<Reply> visit(final PostgresCreateMessage message,
      final VisitorContext context)
  {
    log.debug("Creating Postgres Database");
    List<BaseResource> resources = resourceCorrelationMap.get(context.getId());

    Pnky<Reply> callback = Pnky.create();
    if (resources != null)
    {

      PostgresResource postgresResource =
          resourceFactory.getPostgresResource(configuration.getHap(), configuration.getUsername(),
              configuration.getPassword());

      resources.add(postgresResource);
      try
      {
        postgresResource.init();
        log.debug("Created Postgres Database");
        callback.resolve(new ConnectionInformationReply(Lists
            .newArrayList(new ConnectionInformation(
                "postgres", postgresResource.getId(), postgresResource.getHost(), postgresResource
                    .getPort(), new UsernamePasswordCredential(postgresResource.getUsername(),
                    postgresResource.getPassword())))));
      }
      catch (ResourceInitializationException e)
      {
        callback.reject(e);
      }
    }
    else
    {
      callback.reject(new NullPointerException(
          "The Test session has not been registered to the correlation map"));
    }

    return callback;
  }

  @Override
  public PnkyPromise<Reply> visit(final PostgresExecSqlMessage message,
      final VisitorContext context)
  {
    Pnky<Reply> callback = Pnky.create();
    PostgresResource resource =
        getResourceOfId(message.getDatabaseId(), resourceCorrelationMap.get(context.getId()));

    if (resource != null)
    {
      try
      {
        resource.executeQuery(message.getSql());
        callback.resolve(new SuccessReply());
      }
      catch (SQLException e)
      {
        callback.reject(e);
      }
    }
    else
    {
      callback.reject(new NullPointerException(
          "The resource id " + message.getDatabaseId() + " does not exist for the test id "
              + context.getId()));
    }

    return callback;
  }

  @Override
  public PnkyPromise<Reply> visit(final PostgresRestoreMessage message,
      final VisitorContext context)
  {
    Pnky<Reply> callback = Pnky.create();

    PostgresResource resource =
        getResourceOfId(message.getDatabaseId(), resourceCorrelationMap.get(context.getId()));

    if (resource != null)
    {
      File fileToRestore =
          new File(message.getDatabaseId() + RandomStringUtils.randomAlphabetic(10).toLowerCase()
              + ".sql");

      try
      {
        if (!fileToRestore.exists())
        {
          fileToRestore.createNewFile();
        }

        FileWriter fw = new FileWriter(fileToRestore.getAbsoluteFile());
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write(message.getPgdump());
        bw.close();
        resource.restoreDump(fileToRestore);

        callback.resolve(new SuccessReply());
      }
      catch (Exception ex)
      {
        callback.reject(ex);
      }
    }
    else
    {
      callback.reject(new NullPointerException(
          "The resource id " + message.getDatabaseId() + " does not exist for the test id "
              + context.getId()));
    }

    return callback;
  }

  /**
   * 
   * @param resourceId
   *          the id of the resource that should be returned
   * @param resources
   *          the list of resources to search
   * @return the resource from resources that matches the resourceId
   */
  public PostgresResource getResourceOfId(final ResourceId resourceId,
      final List<BaseResource> resources)
  {
    for (BaseResource resource : resources)
    {
      if (resource.getId().equals(resourceId) && resource instanceof PostgresResource)
      {
        return (PostgresResource) resource;
      }
    }
    return null;
  }

}

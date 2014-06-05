package com.jive.qa.dreidel.rabbi.views;

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
import com.jive.myco.commons.callbacks.CallbackFuture;
import com.jive.myco.commons.callbacks.ChainedFuture;
import com.jive.qa.dreidel.api.exceptions.ResourceInitializationException;
import com.jive.qa.dreidel.api.interfaces.PostgresVisitor;
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

@AllArgsConstructor(onConstructor = @__(@Inject))
@Slf4j
public class PostgresVisitorImpl implements
    PostgresVisitor<Reply, VisitorContext>
{

  private final PostgresConfiguration configuration;
  private final Map<String, List<BaseResource>> resourceCorrelationMap;
  private final ResourceFactory resourceFactory;

  @Override
  public ChainedFuture<Reply> visit(final PostgresCreateMessage message,
      final VisitorContext context)
  {
    log.debug("Creating Postgres Database");
    List<BaseResource> resources = resourceCorrelationMap.get(context.getId());

    CallbackFuture<Reply> callback = new CallbackFuture<>();
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
        callback.onSuccess(new ConnectionInformationReply(Lists
            .newArrayList(new ConnectionInformation(
                "postgres", postgresResource.getId(), postgresResource.getHost(), postgresResource
                    .getPort(), new UsernamePasswordCredential(postgresResource.getUsername(),
                    postgresResource.getPassword())))));
      }
      catch (ResourceInitializationException e)
      {
        callback.onFailure(e);
      }
    }
    else
    {
      callback.onFailure(new NullPointerException(
          "The Test session has not been registered to the correlation map"));
    }

    return callback;
  }

  @Override
  public ChainedFuture<Reply> visit(final PostgresExecSqlMessage message,
      final VisitorContext context)
  {
    CallbackFuture<Reply> callback = new CallbackFuture<>();
    PostgresResource resource =
        getResourceOfId(message.getId(), resourceCorrelationMap.get(context.getId()));

    if (resource != null)
    {
      try
      {
        resource.executeQuery(message.getSql());
        callback.onSuccess(new SuccessReply());
      }
      catch (SQLException e)
      {
        callback.onFailure(e);
      }
    }
    else
    {
      callback.onFailure(new NullPointerException(
          "The resource id " + message.getId() + " does not exist for the test id "
              + context.getId()));
    }

    return callback;
  }

  @Override
  public ChainedFuture<Reply> visit(final PostgresRestoreMessage message,
      final VisitorContext context)
  {
    CallbackFuture<Reply> callback = new CallbackFuture<>();

    PostgresResource resource =
        getResourceOfId(message.getId(), resourceCorrelationMap.get(context.getId()));

    if (resource != null)
    {
      File fileToRestore =
          new File(message.getId() + RandomStringUtils.randomAlphabetic(10).toLowerCase() + ".sql");

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

        callback.onSuccess(new SuccessReply());
      }
      catch (Exception ex)
      {
        callback.onFailure(ex);
      }
    }
    else
    {
      callback.onFailure(new NullPointerException(
          "The resource id " + message.getId() + " does not exist for the test id "
              + context.getId()));
    }

    return callback;
  }

  public PostgresResource getResourceOfId(final String resourceId,
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

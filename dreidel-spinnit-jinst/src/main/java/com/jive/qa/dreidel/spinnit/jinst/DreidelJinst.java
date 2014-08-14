package com.jive.qa.dreidel.spinnit.jinst;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.net.HostAndPort;
import com.jive.myco.commons.concurrent.Pnky;
import com.jive.myco.commons.concurrent.PnkyPromise;
import com.jive.qa.dreidel.api.messages.ConnectionInformationMessage;
import com.jive.qa.dreidel.api.messages.ExceptionMessage;
import com.jive.qa.dreidel.api.messages.ReplyMessage;
import com.jive.qa.dreidel.api.messages.jinst.JinstCreateMessage;
import com.jive.qa.dreidel.api.replies.ConnectionInformation;
import com.jive.qa.dreidel.spinnit.api.DreidelConnection;
import com.jive.qa.dreidel.spinnit.api.DreidelConnectionException;
import com.jive.qa.dreidel.spinnit.api.DreidelSpinner;
import com.jive.v5.commons.rest.client.RestClient;

@Slf4j
public class DreidelJinst
{

  private final DreidelSpinner spinner;
  private DreidelConnection connection;
  private final String logprefix;
  private final String jClass;
  private GoyimJinstResource endpoint;
  private ExecutorService executor = Executors.newSingleThreadExecutor();
  private final String branch;

  @Getter
  private String dreidelId;
  @Getter
  private String host;

  public DreidelJinst(String id, HostAndPort hap, String jClass)
  {
    this(id, hap, jClass, new DreidelSpinner(id, hap), "master");
  }

  public DreidelJinst(String id, HostAndPort hap, String jClass, String branch)
  {
    this(id, hap, jClass, new DreidelSpinner(id, hap), branch);
  }

  DreidelJinst(String id, HostAndPort hap, String jClass, DreidelSpinner spinner, String branch)
  {
    this.logprefix = "[" + id + "]";
    this.spinner = spinner;
    // TODO NULL CHECK
    this.jClass = jClass;
    this.branch = branch;
  }

  public PnkyPromise<Void> spin(int timeoutInMinutes) throws DreidelConnectionException
  {
    Pnky<Void> promise = Pnky.create();

    log.debug("{} Spinning up a dreidel jinst server", logprefix);
    // TODO state checking because we don't want to kill a connection if we are already connected.
    connection = spinner.connect();
    if (connection != null)
    {
      executor.execute(() ->
      {
        log.debug("{} Connected to dreidel ", logprefix);
        ReplyMessage reply = null;

        try
        {
          // TODO remove hard coded timeout
          reply =
              connection.writeRequest(new JinstCreateMessage(UUID.randomUUID().toString(), jClass,
                  branch),
                  timeoutInMinutes, TimeUnit.MINUTES);
          log.debug("{} Recieved reply to creation message {}", logprefix, reply);
        }
        catch (Exception e)
        {
          promise.reject(new DreidelConnectionException(
              "There was a problem sending the creation message to the dreidel server", e));
          return;
        }
        if (reply instanceof ExceptionMessage)
        {
          promise.reject(new DreidelConnectionException(((ExceptionMessage) reply)
              .getExceptionMessage()));
        }
          else
          {
            log.debug("{} wiring up ConnectionInformation", logprefix);
            // TODO this is where we would allow multiple connections to be returned for a single
            // jinst
            // instantiation
            ConnectionInformation information =
                ((ConnectionInformationMessage) reply).getConnections().get(0);
            this.dreidelId = information.getId().toString();
            this.host = information.getHost();
            CloseableHttpAsyncClient client = HttpAsyncClients.createMinimal();
            client.start();
            this.endpoint =
                new RestClient(client, new ObjectMapper()).bind(
                    "http://" + host + ":8018",
                    GoyimJinstResource.class);
            promise.resolve(null);
          }
        });

    }
    return promise;
  }

  public void setPropertiesAndRestart(Map<String, String> properties, String filePath,
      String serviceName)
      throws InterruptedException, ExecutionException
  {
    endpoint.setProperties(properties, filePath).get();
    endpoint.restartService(serviceName).get();
  }

  public boolean getServiceStatus(String serviceName) throws InterruptedException,
      ExecutionException
  {
    return endpoint.getServiceStatus(serviceName).get().contains("is running.");
  }
}

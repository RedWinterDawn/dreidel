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
import com.jive.myco.jazz.rest.client.DefaultRestClientFactory;
import com.jive.myco.jazz.rest.client.JacksonJsonRestClientSerializer;
import com.jive.qa.dreidel.api.messages.ConnectionInformationMessage;
import com.jive.qa.dreidel.api.messages.ExceptionMessage;
import com.jive.qa.dreidel.api.messages.ReplyMessage;
import com.jive.qa.dreidel.api.messages.jinst.JinstCreateMessage;
import com.jive.qa.dreidel.api.replies.ConnectionInformation;
import com.jive.qa.dreidel.spinnit.api.DreidelConnection;
import com.jive.qa.dreidel.spinnit.api.DreidelConnectionException;
import com.jive.qa.dreidel.spinnit.api.DreidelSpinner;
import com.jive.qa.dreidel.spinnit.jinst.exceptions.DreidelBuilderException;

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

  /**
   * Creates a new Connection to Dreidel so you can build an instance of a jinst class. Uses the
   * default branch of master.
   *
   * @param id
   *          The logging ID.
   * @param hap
   *          The Host and Port of the Dreidel server.
   * @param jClass
   *          The Host and Port of the Dreidel server.
   */
  public DreidelJinst(String id, HostAndPort hap, String jClass)
  {
    this(id, hap, jClass, new DreidelSpinner(id, hap), "master");
  }

  /**
   * Creates a new Connection to Dreidel so you can build an instance of a jinst class.
   *
   * @param id
   *          The logging ID.
   * @param hap
   *          The Host and Port of the Dreidel server.
   * @param jClass
   *          The Host and Port of the Dreidel server.
   * @param branch
   *          The branch you are testing in.
   */
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

  /**
   * Starts the process of creating a jinstance using jim/dreidel
   *
   * @param timeoutInMinutes
   *          how long you want to wait for the jinstance to boot before timing out
   * @throws DreidelConnectionException
   *           If there is a problem connecting to dreidel
   */
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
          this.endpoint = new DefaultRestClientFactory(client)
              .bind(GoyimJinstResource.class)
              .addRestClientSerializer(new JacksonJsonRestClientSerializer(new ObjectMapper()))
              .url("http://" + host + ":8018")
              .build();
          promise.resolve(null);
        }
      });

    }
    return promise;
  }

  /**
   * Changes the values of a properties file.
   *
   * Values contained in properties that are not on the server are added. Values contained in
   * properties that are on the server are overwritten. Values not contained in properties that are
   * on the server are left alone.
   *
   * This also restarts the service after the properties are changed so their changes will reflect
   * in the running service.
   *
   * @param properties
   *          the properties you wish to set
   * @param filePath
   *          the path to the properties file
   * @param serviceName
   *          the name of the service you wish to restart
   */
  public void setPropertiesAndRestart(Map<String, String> properties, String filePath,
      String serviceName)
      throws InterruptedException, ExecutionException
  {
    endpoint.setProperties(properties, filePath).get();
    endpoint.restartService(serviceName).get();
  }

  /**
   * Gets the services status
   *
   * @param serviceName
   *          the name of the service in question
   * @return true if service is running false otherwise
   */
  public boolean getServiceStatus(String serviceName) throws InterruptedException,
      ExecutionException
  {
    return endpoint.getServiceStatus(serviceName).get().contains("is running.");
  }

  public static class DreidelJinstBuilder
  {
    private HostAndPort hap = null;
    private String jinstClass = null;
    private String branch = null;
    private String id = null;

    /**
     *
     * @return A new instance of a DreidelJinstBuilder.
     */
    public static DreidelJinstBuilder builder()
    {
      return new DreidelJinstBuilder();
    }

    /**
     *
     * @param hap
     *          The Host and Port of the Dreidel server.
     *
     */
    public DreidelJinstBuilder hap(HostAndPort hap)
    {
      this.hap = hap;
      return this;
    }

    /**
     *
     * @param jinstClass
     *          The jinst class you are testing.
     *
     */
    public DreidelJinstBuilder jinstClass(String jinstClass)
    {
      this.jinstClass = jinstClass;
      return this;
    }

    /**
     * This sets the branch of the jinst class you are using. The default is master.
     *
     *
     * @param branch
     *          The branch you are testing in.
     *
     */
    public DreidelJinstBuilder branch(String branch)
    {
      this.branch = branch;
      return this;
    }

    /**
     * This is a shortcut for prepending "workspace/" to your workspace.
     *
     * For instance if your workspace name is "US999-testing" your branch would be
     * "workspace/US999-testing". If you pass "US999-testing" to this function it will set your
     * branch to "workspace/US999-testing".
     *
     * @param workspace
     *          The name of the workspace.
     */
    public DreidelJinstBuilder workspace(String workspace)
    {
      this.branch = "workspace/" + workspace;
      return this;
    }

    /**
     * This prepends your logging ID in front of all jinst-related logs for this instance. Default
     * is a 10 digit alphanumeric.
     *
     * @param id
     *          The logging ID.
     *
     */
    public DreidelJinstBuilder id(String id)
    {
      this.id = id;
      return this;
    }

    /**
     * Returns a new Dreidel jinst. The minimum you need is a host and port and a jinst class.
     *
     *
     */
    public DreidelJinst build()
    {
      if (hap == null || jinstClass == null)
      {
        throw new DreidelBuilderException("host and port and jClass must both be set");
      }
      if (branch == null)
      {
        branch = "master";
      }
      if (id == null)
      {
        id = UUID.randomUUID().toString().replace("-", "").substring(0, 10);
      }
      return new DreidelJinst(id, hap, jinstClass, branch);
    }
  }

}

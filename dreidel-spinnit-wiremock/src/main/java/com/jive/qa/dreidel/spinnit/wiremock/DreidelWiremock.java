package com.jive.qa.dreidel.spinnit.wiremock;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import com.google.common.net.HostAndPort;
import com.jive.qa.dreidel.api.messages.ConnectionInformationMessage;
import com.jive.qa.dreidel.api.messages.ExceptionMessage;
import com.jive.qa.dreidel.api.messages.ReplyMessage;
import com.jive.qa.dreidel.api.messages.wiremock.WiremockCreateMessage;
import com.jive.qa.dreidel.api.replies.ConnectionInformation;
import com.jive.qa.dreidel.spinnit.api.DreidelConnection;
import com.jive.qa.dreidel.spinnit.api.DreidelConnectionException;
import com.jive.qa.dreidel.spinnit.api.DreidelSpinner;

@Slf4j
public class DreidelWiremock
{

  private final DreidelSpinner spinner;
  private DreidelConnection connection;
  private final String logprefix;
  private final HostAndPort hap;

  @Getter
  private String dreidelId;

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
  public DreidelWiremock(String id, HostAndPort hap)
  {
    this(id, hap, new DreidelSpinner(id, hap));
  }

  DreidelWiremock(String id, HostAndPort hap, DreidelSpinner spinner)
  {
    this.logprefix = "[" + id + "]";
    this.spinner = spinner;
    this.hap = hap;
  }

  /**
   * Starts a new wiremock server on the dreidel server
   *
   * @throws DreidelConnectionException
   *           If there is a problem connecting to dreidel
   */
  public WiremockConfigurator spin() throws DreidelConnectionException
  {
    log.debug("{} Spinning up a dreidel wiremock server", logprefix);
    // TODO state checking because we don't want to kill a connection if we are already connected.
    connection = spinner.connect();
    if (connection != null)
    {
      log.debug("{} Connected to dreidel ", logprefix);
      ReplyMessage reply = null;
      try
      {
        // TODO remove hard coded timeout
        reply =
            connection.writeRequest(new WiremockCreateMessage(UUID.randomUUID().toString()),
                10, TimeUnit.SECONDS);
        log.debug("{} Recieved reply to creation message {}", logprefix, reply);
      }
      catch (Exception e)
      {
        throw new DreidelConnectionException(
            "There was a problem sending the creation message to the dreidel server", e);
      }
      if (reply instanceof ExceptionMessage)
      {
        throw new DreidelConnectionException(((ExceptionMessage) reply)
            .getExceptionMessage());
      }
      else
      {
        log.debug("{} wiring up ConnectionInformation", logprefix);
        ConnectionInformation information =
            ((ConnectionInformationMessage) reply).getConnections().get(0);
        this.dreidelId = information.getId().toString();
        return new WiremockConfigurator(HostAndPort.fromParts(hap.getHostText(), hap.getPort()));
      }
    }
    else
    {
      throw new DreidelConnectionException("The connection was null...uh-oh");
    }
  }

}

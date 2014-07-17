package com.jive.qa.dreidel.spinnit.jinst;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import com.google.common.net.HostAndPort;
import com.jive.qa.dreidel.api.messages.ConnectionInformationMessage;
import com.jive.qa.dreidel.api.messages.ExceptionMessage;
import com.jive.qa.dreidel.api.messages.ReplyMessage;
import com.jive.qa.dreidel.api.messages.jinst.JinstCreateMessage;
import com.jive.qa.dreidel.api.replies.ConnectionInformation;
import com.jive.qa.dreidel.spinnit.api.DreidelConnection;
import com.jive.qa.dreidel.spinnit.api.DreidelConnectionException;
import com.jive.qa.dreidel.spinnit.api.DreidelSpinner;

@Slf4j
public class DreidelJinst
{

  private final DreidelSpinner spinner;
  private DreidelConnection connection;
  private final String logprefix;
  private final String jClass;

  @Getter
  private String dreidelId;
  @Getter
  private String host;

  public DreidelJinst(String id, HostAndPort hap, String jClass)
  {
    this.logprefix = "[" + id + "]";
    this.spinner = new DreidelSpinner(id, hap);
    // TODO NULL CHECK
    this.jClass = jClass;
  }

  DreidelJinst(String id, HostAndPort hap, String jClass, DreidelSpinner spinner)
  {
    this.logprefix = "[" + id + "]";
    this.spinner = spinner;
    // TODO NULL CHECK
    this.jClass = jClass;
  }

  public void spin(int timeoutInMinutes) throws DreidelConnectionException
  {
    log.debug("{} Spinning up a dreidel jinst server", logprefix);
    // TODO state checking because we don't want to kill a connection if we are already connected.
    connection = spinner.connect();
    if (connection != null)
    {
      log.debug("{} Connected to dreidel ", logprefix);
      ReplyMessage reply;

      try
      {
        // TODO remove hard coded timeout
        reply =
            connection.writeRequest(new JinstCreateMessage(UUID.randomUUID().toString(), jClass),
                timeoutInMinutes, TimeUnit.MINUTES);
        log.debug("{} Recieved reply to creation message {}", logprefix, reply);
      }
      catch (Exception e)
      {
        throw new DreidelConnectionException(
            "There was a problem sending the creation message to the dreidel server", e);
      }
      if (reply instanceof ExceptionMessage)
      {
        throw new DreidelConnectionException(((ExceptionMessage) reply).getExceptionMessage());
      }
      else
      {
        log.debug("{} wiring up ConnectionInformation", logprefix);
        // TODO this is where we would allow multiple connections to be returned for a single jinst
        // instantiation
        ConnectionInformation information =
            ((ConnectionInformationMessage) reply).getConnections().get(0);
        this.dreidelId = information.getId().toString();
        this.host = information.getHost();
      }

    }

  }
}

package com.jive.qa.dreidel.spinnit.postgres;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;

import com.google.common.collect.Lists;
import com.google.common.net.HostAndPort;
import com.jive.qa.dreidel.api.messages.ConnectionInformationMessage;
import com.jive.qa.dreidel.api.messages.ResourceId;
import com.jive.qa.dreidel.api.replies.ConnectionInformation;
import com.jive.qa.dreidel.api.replies.UsernamePasswordCredential;
import com.jive.qa.dreidel.spinnit.api.DreidelConnection;
import com.jive.qa.dreidel.spinnit.api.DreidelConnectionException;
import com.jive.qa.dreidel.spinnit.api.DreidelSpinner;
import com.jive.qa.dreidel.spinnit.postgres.mocks.MockedDreidelConnection;

public class DreidelPostgres_Test
{

  @Test(expected = DreidelConnectionException.class)
  public void spin_NoConnectionInformation_ThrowsEception() throws DreidelConnectionException
  {

    DreidelConnection connection =
        new MockedDreidelConnection(new ConnectionInformationMessage("foo", Lists.newArrayList()));

    DreidelSpinner spinner = mock(DreidelSpinner.class);

    when(spinner.connect()).thenReturn(connection);

    DreidelPostgres postgres =
        new DreidelPostgres("foo", HostAndPort.fromParts("localhost", 8020), spinner);

    postgres.spin();
  }

  @Test
  public void spin_shouldNotThrowAnError() throws DreidelConnectionException
  {

    DreidelConnection connection =
        new MockedDreidelConnection(new ConnectionInformationMessage("foo",
            Lists.newArrayList(new ConnectionInformation("postgres", ResourceId
                .valueOf("asdfasdf23411234"), "foohost",
                123, new UsernamePasswordCredential("foo", "bar")))));

    DreidelSpinner spinner = mock(DreidelSpinner.class);

    when(spinner.connect()).thenReturn(connection);

    DreidelPostgres postgres =
        new DreidelPostgres("foo", HostAndPort.fromParts("localhost", 8020), spinner);

    postgres.spin();
  }

  @Test(expected = DreidelConnectionException.class)
  public void spin_WhenRecievingNullConnection_ShouldThrowConnectionError()
      throws DreidelConnectionException
  {
    DreidelSpinner spinner = mock(DreidelSpinner.class);

    DreidelPostgres postgres =
        new DreidelPostgres("foo", HostAndPort.fromParts("localhost", 8020), spinner);

    postgres.spin();
  }

}

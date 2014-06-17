package com.jive.qa.dreidel.goyim.views;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;

import javax.ws.rs.core.Response;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jive.qa.dreidel.goyim.controllers.InstanceManager;
import com.jive.qa.dreidel.goyim.controllers.JimController;
import com.jive.qa.dreidel.goyim.exceptions.JimCreationException;
import com.jive.qa.dreidel.goyim.exceptions.JimDestructionException;
import com.jive.qa.dreidel.goyim.messages.IdResponse;
import com.jive.qa.dreidel.goyim.mocks.MockSettings;
import com.jive.qa.dreidel.goyim.models.Instance;
import com.jive.qa.dreidel.goyim.restinator.DreidelObjectMapper;

public class DreidelView_CreateServer_Tests
{

  @Test
  public void successfulCreation_UsesCorrectInstanceAndIp() throws IOException
  {
    JimController jimController = mock(JimController.class);

    when(jimController.serviceExists("bogus")).thenReturn(true);

    DreidelView dreidelView =
        new DreidelView(new DreidelObjectMapper(), new InstanceManager(MockSettings.getBm(),
            MockSettings.getJim()), MockSettings.getBm(), jimController);

    Response response = dreidelView.createServer("bogus");

    assertEquals(200, response.getStatus());
    assertEquals(0,
        new DreidelObjectMapper().readValue(response.getEntity().toString(), IdResponse.class)
            .getId());
    assertEquals("10.20.26.2",
        new DreidelObjectMapper().readValue(response.getEntity().toString(), IdResponse.class)
            .getAddress());
  }

  @Test
  public void unknownService_Returns404() throws JsonProcessingException
  {
    JimController jimController = mock(JimController.class);

    DreidelView dreidelView =
        new DreidelView(new DreidelObjectMapper(), new InstanceManager(MockSettings.getBm(),
            MockSettings.getJim()), MockSettings.getBm(), jimController);

    Response response = dreidelView.createServer("bogus");

    assertEquals(404, response.getStatus());

  }

  @Test
  public void creationException_Returns500() throws JsonProcessingException, JimCreationException,
      JimDestructionException
  {
    JimController jimController = mock(JimController.class);

    when(jimController.serviceExists("something")).thenReturn(true);

    InstanceManager instanceManager = mock(InstanceManager.class);

    Instance instance = mock(Instance.class);

    when(instanceManager.getNextAvailableInstance()).thenReturn(instance);

    doThrow(new JimCreationException("something is wrong")).when(jimController).createInstance(
        "something", instance);

    DreidelView dreidelView =
        new DreidelView(new DreidelObjectMapper(), instanceManager, MockSettings.getBm(),
            jimController);

    Response response = dreidelView.createServer("something");

    assertEquals(500, response.getStatus());

  }

  @Test
  public void creationException_DoesntCreateNewInstanceInInstanceManager()
      throws JsonProcessingException, JimCreationException, JimDestructionException
  {
    JimController jimController = mock(JimController.class);

    when(jimController.serviceExists("something")).thenReturn(true);

    InstanceManager instanceManager =
        new InstanceManager(MockSettings.getBm(), MockSettings.getJim());

    doThrow(new JimCreationException("something is wrong")).when(jimController).createInstance(
        any(String.class), any(Instance.class));

    DreidelView dreidelView =
        new DreidelView(new DreidelObjectMapper(), instanceManager, MockSettings.getBm(),
            jimController);

    Response response = dreidelView.createServer("something");

    assertEquals(500, response.getStatus());
    assertEquals(0, instanceManager.getNextAvailableInstance().getInstance());
  }

}

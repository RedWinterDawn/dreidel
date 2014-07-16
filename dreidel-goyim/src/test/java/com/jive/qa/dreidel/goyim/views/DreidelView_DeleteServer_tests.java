package com.jive.qa.dreidel.goyim.views;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.io.IOException;

import javax.ws.rs.core.Response;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.Maps;
import com.jive.qa.dreidel.goyim.controllers.InstanceManager;
import com.jive.qa.dreidel.goyim.controllers.JimController;
import com.jive.qa.dreidel.goyim.exceptions.JimDestructionException;
import com.jive.qa.dreidel.goyim.jim.JimService;
import com.jive.qa.dreidel.goyim.mocks.MockSettings;
import com.jive.qa.dreidel.goyim.restinator.DreidelObjectMapper;

public class DreidelView_DeleteServer_tests
{
  @Test
  public void successfulDeletion_UsesCorrectInstanceAndIp() throws IOException
  {
    JimController jimController = mock(JimController.class);
    JimService jimService = mock(JimService.class);

    when(jimService.serviceExists("bogus")).thenReturn(true);

    when(jimService.instanceExists("bogus", 0, "ops-1a")).thenReturn(true);

    DreidelView dreidelView =
        new DreidelView(new DreidelObjectMapper(), new InstanceManager(MockSettings.getBm(),
            MockSettings.getJim()), MockSettings.getBm(), jimController, jimService,
            Maps.newConcurrentMap());

    Response response2 =
        dreidelView.deleteServer("bogus", 0);

    assertEquals(200, response2.getStatus());

  }

  @Test
  public void unknownInstance_Returns404() throws JsonProcessingException
  {
    JimController jimController = mock(JimController.class);
    JimService jimService = mock(JimService.class);

    when(jimService.serviceExists("bogus")).thenReturn(true);

    DreidelView dreidelView =
        new DreidelView(new DreidelObjectMapper(), new InstanceManager(MockSettings.getBm(),
            MockSettings.getJim()), MockSettings.getBm(), jimController, jimService,
            Maps.newConcurrentMap());

    Response response2 =
        dreidelView.deleteServer("bogus", 0);

    assertEquals(404, response2.getStatus());
  }

  @Test
  public void unknownService_Returns404() throws JsonProcessingException
  {
    JimController jimController = mock(JimController.class);
    JimService jimService = mock(JimService.class);

    DreidelView dreidelView =
        new DreidelView(new DreidelObjectMapper(), new InstanceManager(MockSettings.getBm(),
            MockSettings.getJim()), MockSettings.getBm(), jimController, jimService,
            Maps.newConcurrentMap());

    Response response2 =
        dreidelView.deleteServer("bogus", 0);

    assertEquals(404, response2.getStatus());
  }

  @Test
  public void exceptionIsThrown_Returns500() throws JsonProcessingException,
      JimDestructionException
  {
    JimController jimController = mock(JimController.class);
    JimService jimService = mock(JimService.class);

    when(jimService.serviceExists("bogus")).thenReturn(true);

    when(jimService.instanceExists("bogus", 0, "ops-1a")).thenReturn(true);

    doThrow(new JimDestructionException("message")).when(jimController).deleteInstance(
        any(String.class), any(int.class));

    DreidelView dreidelView =
        new DreidelView(new DreidelObjectMapper(), new InstanceManager(MockSettings.getBm(),
            MockSettings.getJim()), MockSettings.getBm(), jimController, jimService,
            Maps.newConcurrentMap());

    Response response2 =
        dreidelView.deleteServer("bogus", 0);

    assertEquals(500, response2.getStatus());
  }

}

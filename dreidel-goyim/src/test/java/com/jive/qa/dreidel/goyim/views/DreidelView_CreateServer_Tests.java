package com.jive.qa.dreidel.goyim.views;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.concurrent.ExecutionException;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jive.qa.dreidel.goyim.controllers.JimController;
import com.jive.qa.dreidel.goyim.exceptions.JimCreationException;
import com.jive.qa.dreidel.goyim.exceptions.JimDestructionException;
import com.jive.qa.dreidel.goyim.exceptions.ServiceNotFoundException;

public class DreidelView_CreateServer_Tests
{
  @Test(expected = ServiceNotFoundException.class)
  public void unknownService_ThrowsException() throws JsonProcessingException,
      ServiceNotFoundException,
      InterruptedException, ExecutionException, JimCreationException, JimDestructionException
  {
    final JimController jimController = mock(JimController.class);
    //
    // final DreidelView dreidelView =
    // new DreidelView(
    // MockSettings.getBm(), jimController,
    // Maps.newConcurrentMap());
    //
    // dreidelView.createServer("bogus");

    fail();

  }

  @Test(expected = JimCreationException.class)
  public void creationException_ThrowsException() throws JsonProcessingException,
      JimCreationException,
      JimDestructionException, ServiceNotFoundException, InterruptedException, ExecutionException
  {
    final JimController jimController = mock(JimController.class);

    when(jimController.serviceExists("something")).thenReturn(true);

    doThrow(new JimCreationException("something is wrong")).when(jimController).createInstance(
        "something", "ops-1a");

    // final DreidelView dreidelView =
    // // new DreidelView(MockSettings.getBm(),
    // // jimController, Maps.newConcurrentMap());
    //
    // // dreidelView.createServer("something");

    fail();

  }

  @Test(expected = JimCreationException.class)
  public void creationException_DoesntCreateNewInstanceInInstanceManager()
      throws JsonProcessingException, JimCreationException, JimDestructionException,
      ServiceNotFoundException, InterruptedException, ExecutionException
  {
    final JimController jimController = mock(JimController.class);

    when(jimController.serviceExists("something")).thenReturn(true);



    doThrow(new JimCreationException("something is wrong")).when(jimController).createInstance(
        any(String.class), any(String.class));

    // final DreidelView dreidelView =
    // new DreidelView(MockSettings.getBm(),
    // jimController, Maps.newConcurrentMap());
    //
    // dreidelView.createServer("something");

    fail();
  }

}

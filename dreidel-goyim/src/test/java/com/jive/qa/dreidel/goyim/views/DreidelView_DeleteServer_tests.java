package com.jive.qa.dreidel.goyim.views;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import javax.management.InstanceNotFoundException;

import org.junit.Test;

import com.jive.qa.dreidel.goyim.controllers.JimController;
import com.jive.qa.dreidel.goyim.exceptions.JimDestructionException;
import com.jive.qa.dreidel.goyim.exceptions.ServiceNotFoundException;

public class DreidelView_DeleteServer_tests
{
  @Test(expected = InstanceNotFoundException.class)
  public void unknownInstance_ThrowsException() throws Exception
  {
    final JimController jimController = mock(JimController.class);

    when(jimController.serviceExists("bogus")).thenReturn(true);

    // final DreidelView dreidelView =
    // new DreidelView(MockSettings.getBm(), jimController,
    // Maps.newConcurrentMap());
    //
    // dreidelView.deleteServer("bogus", "garbage");
    fail();

  }

  @Test(expected = ServiceNotFoundException.class)
  public void unknownService_ThrowsException() throws Exception
  {
    final JimController jimController = mock(JimController.class);

    // final DreidelView dreidelView =
    // new DreidelView(
    // MockSettings.getBm(), jimController,
    // Maps.newConcurrentMap());
    //
    // dreidelView.deleteServer("bogus", "garbage");
    fail();
  }

  @Test(expected = JimDestructionException.class)
  public void exceptionIsThrown_ThrowsException() throws Exception
  {
    final JimController jimController = mock(JimController.class);

    when(jimController.serviceExists("bogus")).thenReturn(true);

    when(jimController.instanceExists(anyString())).thenReturn(true);

    doThrow(new JimDestructionException("message")).when(jimController).deleteInstance(
        any(String.class), any(String.class));

    // final DreidelView dreidelView =
    // new DreidelView(
    // MockSettings.getBm(), jimController,
    // Maps.newConcurrentMap());
    //
    // dreidelView.deleteServer("bogus", "garbage");
    fail();
  }

}

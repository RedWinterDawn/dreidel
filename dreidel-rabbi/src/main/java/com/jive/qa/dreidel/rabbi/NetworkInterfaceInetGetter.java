package com.jive.qa.dreidel.rabbi;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;

import lombok.NonNull;

public class NetworkInterfaceInetGetter
{
  public String getInetAddress(@NonNull String inetName) throws SocketException
  {
    return Collections.list(NetworkInterface.getByName(inetName).getInetAddresses()).get(0)
        .getHostAddress();
  }
}

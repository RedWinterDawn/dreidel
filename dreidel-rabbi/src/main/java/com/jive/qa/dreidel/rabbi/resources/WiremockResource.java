package com.jive.qa.dreidel.rabbi.resources;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import com.google.common.net.HostAndPort;
import com.jive.qa.dreidel.api.exceptions.ResourceDestructionException;
import com.jive.qa.dreidel.api.exceptions.ResourceInitializationException;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.InputStreamReader;

@Slf4j
public class WiremockResource extends BaseResourceImpl implements BaseResource
{

  private Process proc;
  private final String jarLocation;

  public WiremockResource(HostAndPort hap, String jarLocation)
  {
    super(hap);
    this.jarLocation = jarLocation;
  }

  @Override
  public void init() throws ResourceInitializationException
  {
    try
    {
      proc =
          new ProcessBuilder("java", "-jar",
              new File(jarLocation, "/wiremock-1.47-standalone.jar").toString(),
              "--port", Integer.toString(getHap().getPort())).start();
      Thread.sleep(1000);

      if (!proc.isAlive() && proc.exitValue() != 0)
      {
        String message = "something went wrong trying to start up the wiremock jar " +
            CharStreams.toString(new InputStreamReader(proc.getErrorStream(), Charsets.UTF_8));
        log.error(message);
        throw new Exception(message);
      }
    }
    catch (Exception e)
    {
      throw new ResourceInitializationException(
          "something went wrong starting up the wiremock process", e);
    }

  }

  @Override
  public void destroy() throws ResourceDestructionException
  {
    if (proc != null)
    {
      proc.destroyForcibly();
    }
  }

}

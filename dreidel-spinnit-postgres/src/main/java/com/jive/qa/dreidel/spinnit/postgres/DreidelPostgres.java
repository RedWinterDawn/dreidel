package com.jive.qa.dreidel.spinnit.postgres;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import lombok.Cleanup;
import lombok.Delegate;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.google.common.io.ByteStreams;
import com.google.common.io.CharSource;
import com.google.common.io.CharStreams;
import com.google.common.io.Files;
import com.google.common.net.HostAndPort;
import com.google.common.net.HttpHeaders;
import com.google.common.net.MediaType;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.jive.jackson.ConstructorPropertiesAnnotationIntrospector;

/**
 * DreidelDB is used to
 * 
 * @author jdavidson
 *
 */
@Slf4j
@RequiredArgsConstructor
public class DreidelPostgres
{
  private static final ObjectMapper json = new ObjectMapper();
  static 
  {
    ConstructorPropertiesAnnotationIntrospector.install(json);
  }
  
  private final String simpleDatabaseName;
  private final HostAndPort server;
  private URL databaseUrl;
  @Delegate
  private DredielConnectionInfo connection;

  public void spin() throws MalformedURLException, IOException
  {
    log.trace("spinning up dreidel connection");
    @Cleanup("disconnect")
    HttpURLConnection connection = (HttpURLConnection) new URL("http://" + server.toString() + "/postgres").openConnection();
    connection.setRequestMethod("POST");
    connection.connect();
    if (connection.getResponseCode() >= 200 && connection.getResponseCode() < 300)
    {
      @Cleanup
      InputStream response = connection.getInputStream();
      this.connection = json.readValue(response, DredielConnectionInfo.class);
      log.trace("got good response from server. database={} response={} unique={}", this.simpleDatabaseName, connection.getResponseCode(), this.connection.databaseName);
      this.databaseUrl = new URL("http://" + this.server + "/postgres/" + this.connection.databaseName);
      
      log.trace("scheduling keep alive for database={}", this.connection.databaseName);
      Executors.newSingleThreadScheduledExecutor(
          new ThreadFactoryBuilder().setNameFormat("drediel-keepalive-" + this.connection.databaseName + "-%d").setDaemon(true).build())
            .scheduleAtFixedRate(new KeepAlive(server, this.connection.databaseName, simpleDatabaseName), KeepAlive.INTERVAL_SECONDS, KeepAlive.INTERVAL_SECONDS, TimeUnit.SECONDS);
    }
  }


  /**
   * Upload schema and/or SQL data to the newly created database.
   * 
   * @param fileToUpload
   *          the file that you want to upload. It must be valid sql with all the pretty ;'s everywhere.
   * @return returns false if the response was not 202 ok.
   * @throws IOException
   * @throws MalformedURLException
   * @throws RemoteSqlExecutionException
   * @throws Exception
   */
  public void executeSql(CharSource source)
  {
    try
    {
      log.trace("beginning uploading of sql source={}", source);
      @Cleanup("disconnect")
      HttpURLConnection connection = (HttpURLConnection) this.databaseUrl.openConnection();
      connection.setRequestMethod("POST");
      connection.setRequestProperty(HttpHeaders.CONTENT_TYPE, MediaType.PLAIN_TEXT_UTF_8.toString());
      connection.setDoOutput(true);
      connection.connect();
      @Cleanup
      OutputStream out = connection.getOutputStream();
      out.write(source.read().getBytes(Charsets.UTF_8));
      if (connection.getResponseCode() != 202)
      {
        log.trace("error uploading sql database={} code={} unique={}", simpleDatabaseName, connection.getResponseCode(), this.connection.databaseName);
        throw new RemoteSqlExecutionException("The file was not uploaded code: " + connection.getResponseCode() + " message: "
            + CharStreams.toString(new InputStreamReader(connection.getErrorStream())));
      }
    }
    catch (IOException ex)
    {
      throw new RemoteSqlExecutionException(ex);
    }
  }
  
  public void executeSqlDirectory(File directory) 
  {
	  for (File fileEntry : directory.listFiles()) 
      {
		  if (!fileEntry.isDirectory())
		  {
			  if(fileEntry.getName().endsWith(".sql"))
			  {
				  CharSource cs = Files.asCharSource(fileEntry, Charsets.UTF_8);
				  executeSql(cs);
			  }
		  }
      }
  }

  /**
   * 
   * @author jdavidson This thread repeatedly sleeps for 10 seconds, then sends a GET to Dreidel to keep the database alive
   */
  @Slf4j
  private static class KeepAlive implements Runnable
  {
    public static final long INTERVAL_SECONDS = 10L;
    private URL server;
    private String database;
    private String name;  

    private KeepAlive(HostAndPort server, String database, String name) throws MalformedURLException
    {
      this.server = new URL("http://" + server + "/postgres/" + database);
      this.database = database;
      this.name = name;
    }
    
    @Override
    public void run()
    {
      log.debug("starting keep alive for database={} unique={}", this.name, this.database);
      try
      {
        @Cleanup("disconnect")
        HttpURLConnection connection = (HttpURLConnection) this.server.openConnection();
        connection.connect();
        int responseCode = connection.getResponseCode();
        log.debug("keep alive for finished for database={} response={} unique={}", name, responseCode, this.database);
      }
      catch (Exception ex)
      {
        log.error("error during keep alive. database={} unique={}", this.name, ex, this.database);
      }
    }
  }

  /**
   * This class mirrors the JSON response when you post to /postgres
   * 
   * @author jdavidson
   *
   */
  @Value
  public static class DredielConnectionInfo
  {
    private String databaseName;
    private int port;
    private String username;
    private String password;
  }

}
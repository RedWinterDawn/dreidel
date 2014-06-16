package com.jive.qa.restinator;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Map.Entry;

import lombok.Cleanup;

import com.google.common.collect.Maps;
import com.jive.qa.restinator.codecs.ByteArrayEndpointCodec;

public class Endpoint<I, O>
{

  private final ByteArrayEndpointCodec<I, O> codec;
  private final Map<String, String> headers;

  public Endpoint(ByteArrayEndpointCodec<I, O> codec)
  {
    this.codec = codec;
    this.headers = Maps.newConcurrentMap();
  }

  public Endpoint(ByteArrayEndpointCodec<I, O> codec, Map<String, String> defaultHeaders)
  {
    this.codec = codec;
    this.headers = defaultHeaders;
  }

  public EP<I, O> url(String url) throws MalformedURLException
  {
    return new EP<I, O>(this.codec, new URL(url), this.headers);
  }

  public EP<I, O> url(URL url)
  {
    return new EP<I, O>(this.codec, url, this.headers);
  }

  public EP<I, O> url(URL url, String spec) throws MalformedURLException
  {
    return new EP<I, O>(this.codec, new URL(url, spec), this.headers);
  }

  public static class EP<I, O>
  {
    private final URL url;
    private final ByteArrayEndpointCodec<I, O> codec;
    private final Map<String, String> defaultHeaders;

    public EP(ByteArrayEndpointCodec<I, O> codec, URL url, Map<String, String> defaultHeaders)
    {
      this.url = url;
      this.codec = codec;
      this.defaultHeaders = defaultHeaders;
    }

    public I get() throws IOException
    {
      @Cleanup("disconnect")
      HttpURLConnection con = (HttpURLConnection) this.url.openConnection();

      con.setRequestMethod("GET");

      addHeadersToConnection(con, this.defaultHeaders);

      con.connect();

      int response = con.getResponseCode();

      InputStream inputStream = con.getInputStream();
      // TODO clean this up
      return this.codec.decode(getStringFromInputStream(inputStream).getBytes(), response);
    }

    public I post(O object, Map<String, String> headers) throws IOException
    {
      return connectWithContent(object, headers, "POST");
    }

    public I post(Map<String, String> headers) throws IOException
    {
      return post(null, headers);
    }

    public I post() throws IOException
    {
      return post(null, null);
    }

    public I delete(O object, Map<String, String> headers) throws IOException
    {
      return connectWithContent(object, headers, "DELETE");
    }

    public I delete(Map<String, String> headers) throws IOException
    {
      return delete(null, headers);
    }

    public I delete() throws IOException
    {
      return delete(null, null);
    }

    private I connectWithContent(O object, Map<String, String> headers, String requestMethod)
        throws IOException
    {
      @Cleanup("disconnect")
      HttpURLConnection con = (HttpURLConnection) this.url.openConnection();

      con.setRequestMethod(requestMethod);

      addHeadersToConnection(con, this.defaultHeaders);
      addHeadersToConnection(con, headers);

      if (object != null)
      {
        con.setDoOutput(true);

        byte[] outBytes = codec.encode(object);
        // TODO if outBytes == null throw some exception

        @Cleanup("close")
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        try
        {
          wr.write(outBytes);
        }
        finally
        {
          wr.flush();
        }
      }
      else
      {
        con.connect();
      }

      int response = con.getResponseCode();

      InputStream inputStream = con.getInputStream();

      return this.codec.decode(getStringFromInputStream(inputStream).getBytes(), response);
    }

    private void addHeadersToConnection(HttpURLConnection con, Map<String, String> headers)
    {
      if (headers != null)
      {
        for (Entry<String, String> entry : headers.entrySet())
        {
          con.addRequestProperty(entry.getKey(), entry.getValue());
        }
      }
    }

    private String getStringFromInputStream(InputStream is)
    {

      BufferedReader br = null;
      StringBuilder sb = new StringBuilder();

      String line;
      try
      {

        br = new BufferedReader(new InputStreamReader(is));
        while ((line = br.readLine()) != null)
        {
          sb.append(line);
        }

      }
      catch (IOException e)
      {
        e.printStackTrace();
      }
      finally
      {
        if (br != null)
        {
          try
          {
            br.close();
          }
          catch (IOException e)
          {
            e.printStackTrace();
          }
        }
      }

      return sb.toString();

    }
  }

}

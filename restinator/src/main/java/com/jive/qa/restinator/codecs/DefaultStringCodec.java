package com.jive.qa.restinator.codecs;


public class DefaultStringCodec implements ByteArrayEndpointCodec<String, String>
{

  @Override
  public byte[] encode(String object)
  {
    return object.getBytes();
  }

  @Override
  public String decode(byte[] bytes, int responseCode)
  {
    return bytes.toString();
  }

}

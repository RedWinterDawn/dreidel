package com.jive.qa.restinator.codecs;

public interface ByteArrayEndpointCodec<I, O>
{
  byte[] encode(O object);

  I decode(byte[] bytes, int responseCode);

}

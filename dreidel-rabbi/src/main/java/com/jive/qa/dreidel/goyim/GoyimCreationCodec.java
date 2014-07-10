package com.jive.qa.dreidel.goyim;

import lombok.AllArgsConstructor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.jive.qa.dreidel.api.messages.goyim.IdResponse;
import com.jive.qa.restinator.codecs.ByteArrayEndpointCodec;

@AllArgsConstructor(onConstructor = @__(@Inject))
public class GoyimCreationCodec implements ByteArrayEndpointCodec<IdResponse, Void>
{

  private final ObjectMapper json;

  @Override
  public byte[] encode(Void object)
  {
    return null;
  }

  @Override
  public IdResponse decode(byte[] bytes, int responseCode)
  {
    try
    {
      return json.readValue(bytes, IdResponse.class);
    }
    catch (Exception e)
    {
      return null;
    }
  }
}

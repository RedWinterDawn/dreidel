package com.jive.qa.dreidel.goyim;

import com.jive.qa.dreidel.api.messages.goyim.ResponseCodeOnly;
import com.jive.qa.restinator.codecs.ByteArrayEndpointCodec;

public class GoyimDeletionCodec implements ByteArrayEndpointCodec<ResponseCodeOnly, Void>
{

  @Override
  public byte[] encode(Void object)
  {
    return null;
  }

  @Override
  public ResponseCodeOnly decode(byte[] bytes, int responseCode)
  {
    return new ResponseCodeOnly(responseCode);
  }

}

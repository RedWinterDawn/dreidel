package com.jive.qa.dreidel.api.transport;

/**
 * BoneYardTransportCodec
 * 
 * This class takes a Json, in the form of a byte[], and generates the appropriate Boneyard Message
 * or it takes a Message, converts it to Json and then into a Byte[]. Exceptions are thrown as the
 * Boneyard Exception Type except in Encode where that format cann't be returned.
 * 
 * Public Message Decode -- converts the Json byte[] to the message format
 * 
 * Public Message Encode -- converts the message format into a Json byte[]
 */
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.google.inject.Inject;
import com.jive.myco.jivewire.api.codec.ByteArrayTransportCodec;
import com.jive.qa.dreidel.api.exceptions.MessageDecodeException;
import com.jive.qa.dreidel.api.messages.Message;

@AllArgsConstructor(onConstructor = @__(@Inject))
@Slf4j
public class DreidelTransportCodec implements
    ByteArrayTransportCodec<Message, Message>
{

  private static final String DEFAULT_PACKAGE_NAME = "com.jive.qa.dreidel.api.messages";

  @Getter
  private final ObjectMapper json;

  @Override
  public Message decode(final byte[] input)
  {
    JsonNode myJson = null;
    Message rtn;
    try
    {
      String message = new String(input);
      // TODO REMOVE!
      log.debug(message);
      myJson = getJson().readTree(message);

      // check to see if it's in a valid format
      if (myJson.getNodeType() == JsonNodeType.OBJECT && myJson.has("type"))
      {
        // now that we know that it's something we can work with we need to grab the type value
        String type = myJson.get("type").textValue();
        // once we have the type we can just return as we can cast the type as a class name
        rtn = (Message) getJson()
            .readValue(input, Class.forName(getClassName(myJson, type)));
      }
      else
      {
        // something's not right
        throw new MessageDecodeException("Invalid format.");
      }// ends if myJson.getNodeType
    }
    catch (Exception e)
    {
      throw new RuntimeException(e);
    }
    return rtn;
  }

  @Override
  public byte[] encode(final Message input)
  {
    try
    {
      return getJson().writeValueAsString(input).getBytes();
    }
    catch (JsonProcessingException e)
    {
      throw new RuntimeException(e);
    }
  }

  public String getClassName(final JsonNode message, final String type)
  {
    if (message.get("type").textValue().contains("Postgres"))
    {
      return DEFAULT_PACKAGE_NAME + ".postgres." + type;
    }
    else
    {
      return DEFAULT_PACKAGE_NAME + "." + type;
    }
  }

}

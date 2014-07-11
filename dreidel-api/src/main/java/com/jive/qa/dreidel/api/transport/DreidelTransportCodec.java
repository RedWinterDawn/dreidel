package com.jive.qa.dreidel.api.transport;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.google.inject.Inject;
import com.jive.myco.jivewire.api.codec.ByteArrayTransportCodec;
import com.jive.qa.dreidel.api.messages.Message;

/**
 * DreidelTransportCodec
 *
 * This class takes a Json, in the form of a byte[], and generates the appropriate Dreidel Message
 * or it takes a Message, converts it to Json and then into a Byte[]. Exceptions are thrown as the
 * RuntimeExceptions to disconnect the connection when an invalid message is sent
 *
 */
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
        throw new RuntimeException("Invalid format.");
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
    else if (message.get("type").textValue().contains("Jinst"))
    {
      return DEFAULT_PACKAGE_NAME + ".jinst." + type;
    }
    else
    {
      return DEFAULT_PACKAGE_NAME + "." + type;
    }
  }

}

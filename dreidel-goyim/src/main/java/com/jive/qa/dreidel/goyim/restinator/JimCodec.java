package com.jive.qa.dreidel.goyim.restinator;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.google.inject.Inject;
import com.jive.qa.dreidel.goyim.messages.InstanceMessage;
import com.jive.qa.dreidel.goyim.messages.JimErrorMessage;
import com.jive.qa.dreidel.goyim.messages.JimInstanceAlreadyExistsMessage;
import com.jive.qa.dreidel.goyim.messages.JimMessage;
import com.jive.qa.dreidel.goyim.messages.JimResponseCodeOnly;
import com.jive.qa.dreidel.goyim.messages.ServiceDetailMessage;
import com.jive.qa.dreidel.goyim.messages.ServiceListMessage;
import com.jive.qa.dreidel.goyim.models.Service;
import com.jive.qa.dreidel.goyim.models.ServiceDetail;
import com.jive.qa.restinator.codecs.ByteArrayEndpointCodec;

@AllArgsConstructor(onConstructor = @__(@Inject))
@Slf4j
public class JimCodec implements ByteArrayEndpointCodec<JimMessage, JimMessage>
{

  private final ObjectMapper json;

  @Override
  public byte[] encode(JimMessage object)
  {
    try
    {
      return json.writeValueAsBytes(object);
    }
    catch (Exception e)
    {
      log.error("there was an encoder exception", e);
      return null;
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public JimMessage decode(byte[] bytes, int responseCode)
  {
    JimMessage rtn = new JimResponseCodeOnly(responseCode);
    try
    {
      if (bytes.length <= 1)
      {
        return new JimResponseCodeOnly(responseCode);
      }
      else if (responseCode == 200)
      {

        JsonNode tree = this.json.readTree(bytes);

        if (tree.getNodeType() == JsonNodeType.ARRAY && tree.get(0).has("classes"))
        {

          List<Service> services = this.json.readValue(bytes, new TypeReference<List<Service>>()
              {
              });
          rtn = new ServiceListMessage(services);
        }
        else if (tree.getNodeType() == JsonNodeType.OBJECT && tree.has("classes"))
        {
          ServiceDetail detail = this.json.readValue(bytes, ServiceDetail.class);

          rtn = new ServiceDetailMessage(detail);
        }
        else if (tree.getNodeType() == JsonNodeType.OBJECT && tree.has("service"))
        {
          rtn = this.json.readValue(bytes, InstanceMessage.class);

        }

      }
      else
      {
        JsonNode tree = this.json.readTree(bytes);
        if (tree.getNodeType() == JsonNodeType.ARRAY && tree.get(0).toString().contains("already"))
        {
          rtn = new JimInstanceAlreadyExistsMessage();
        }
        else if (tree.getNodeType() == JsonNodeType.ARRAY
            && tree.get(0).getNodeType() == JsonNodeType.STRING)
        {
          rtn = new JimErrorList(json.readValue(bytes, List.class));
        }
      }
    }
    catch (Exception e)
    {
      log.error("There was a codec error", e);
      return new JimErrorMessage(responseCode, new String(bytes));
    }
    return rtn;
  }
}

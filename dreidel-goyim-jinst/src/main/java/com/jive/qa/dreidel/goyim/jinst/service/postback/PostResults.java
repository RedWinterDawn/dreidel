package com.jive.qa.dreidel.goyim.jinst.service.postback;

import javax.annotation.PostConstruct;

import lombok.extern.slf4j.Slf4j;

import org.apache.http.nio.client.HttpAsyncClient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.jive.myco.jazz.rest.client.DefaultRestClientFactory;
import com.jive.myco.jazz.rest.client.JacksonJsonRestClientSerializer;
import com.jive.qa.dreidel.api.messages.goyim.GoyimServiceResponse;

@Slf4j
public class PostResults
{
  private final HttpAsyncClient client;
  private final String baseUrl;
  private final ObjectMapper mapper;

  @Inject
  public PostResults(HttpAsyncClient client, @Named("goyimUrl") String baseUrl, ObjectMapper mapper)
  {
    this.client = client;
    this.baseUrl = baseUrl;
    this.mapper = mapper;
  }

  @PostConstruct
  public void sendPost() throws Exception
  {
    final GoyimServiceResource api = new DefaultRestClientFactory(client)
        .bind(GoyimServiceResource.class)
        .addRestClientSerializer(new JacksonJsonRestClientSerializer(mapper))
        .url(baseUrl)
        .build();

    GoyimServiceResponse response;
    try
    {
      response = api.updateService().get();
      if (response.getStatus().equals("success"))
      {

        log.info("Successfully notified the dreidel-goyim-jim");
      }
      else
      {
        log.error("Failed to notify the dreidel-goyim-jim");
      }
    }
    catch (Exception e)
    {
      log.error("Failed to notify the dreidel-goyim-jim", e);
    }

  }
}

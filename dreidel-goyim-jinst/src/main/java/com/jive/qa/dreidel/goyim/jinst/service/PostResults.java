package com.jive.qa.dreidel.goyim.jinst.service;

import javax.annotation.PostConstruct;

import lombok.extern.slf4j.Slf4j;

import org.apache.http.nio.client.HttpAsyncClient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.jive.qa.dreidel.api.messages.goyim.GoyimServiceResponse;
import com.jive.v5.commons.rest.client.RestClient;

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
    RestClient restClient = new RestClient(client, mapper);
    GoyimServiceResource api = restClient.bind(baseUrl, GoyimServiceResource.class);

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

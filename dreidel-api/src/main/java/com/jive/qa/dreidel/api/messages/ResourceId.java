package com.jive.qa.dreidel.api.messages;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

import com.fasterxml.jackson.annotation.JsonValue;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ResourceId
{
  private final String id;

  public static ResourceId valueOf(String id)
  {
    return new ResourceId(id);
  }

  @Override
  @JsonValue
  public String toString()
  {
    return id;
  }
}

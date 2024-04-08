package com.endava.javacommunity.sendservice.mappers;

import com.endava.javacommunity.sendservice.data.model.Batch;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

@Component
public class CustomMapper {

  private final ObjectMapper objectMapper;

  public CustomMapper(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  public Batch deserializeBatch(String json) {
    try {
      return objectMapper.readValue(json, Batch.class);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  public String serializeToJson(Batch batch) {
    try {
      return objectMapper.writeValueAsString(batch);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

}

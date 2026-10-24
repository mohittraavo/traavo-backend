package com.travo.tcms.platform.entity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;

import java.util.ArrayList;
import java.util.List;

abstract class JsonListConverter<T> implements AttributeConverter<List<T>, String> {
  private static final ObjectMapper MAPPER = new ObjectMapper()
      .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

  private final TypeReference<List<T>> type;

  protected JsonListConverter(TypeReference<List<T>> type) {
    this.type = type;
  }

  @Override
  public String convertToDatabaseColumn(List<T> attribute) {
    try {
      return MAPPER.writeValueAsString(attribute == null ? List.of() : attribute);
    } catch (JsonProcessingException exception) {
      throw new IllegalArgumentException("Could not store fee structure details", exception);
    }
  }

  @Override
  public List<T> convertToEntityAttribute(String dbData) {
    if (dbData == null || dbData.isBlank()) {
      return new ArrayList<>();
    }
    try {
      List<T> value = MAPPER.readValue(dbData, type);
      return value == null ? new ArrayList<>() : value;
    } catch (JsonProcessingException exception) {
      throw new IllegalArgumentException("Could not read fee structure details", exception);
    }
  }
}

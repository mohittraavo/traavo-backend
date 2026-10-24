package com.travo.tcms.platform.entity;

import com.fasterxml.jackson.core.type.TypeReference;
import com.travo.tcms.platform.model.FeeLine;
import jakarta.persistence.Converter;

import java.util.List;

@Converter
public class FeeLineListConverter extends JsonListConverter<FeeLine> {
  public FeeLineListConverter() {
    super(new TypeReference<List<FeeLine>>() {
    });
  }
}

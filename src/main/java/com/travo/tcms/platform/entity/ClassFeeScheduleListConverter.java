package com.travo.tcms.platform.entity;

import com.fasterxml.jackson.core.type.TypeReference;
import com.travo.tcms.platform.model.ClassFeeSchedule;
import jakarta.persistence.Converter;

import java.util.List;

@Converter
public class ClassFeeScheduleListConverter extends JsonListConverter<ClassFeeSchedule> {
  public ClassFeeScheduleListConverter() {
    super(new TypeReference<List<ClassFeeSchedule>>() {
    });
  }
}

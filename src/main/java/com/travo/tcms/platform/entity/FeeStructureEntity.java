package com.travo.tcms.platform.entity;

import com.travo.tcms.platform.model.ClassFeeSchedule;
import com.travo.tcms.platform.model.FeeLine;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tcms_fee_structures")
public class FeeStructureEntity {
  @Id
  public String id;

  public String name;
  public String academicYear;
  public String campus;
  public String effectiveFrom;
  public String effectiveTo;
  public String status;

  @Column(precision = 14, scale = 2)
  public BigDecimal defaultConcession;

  @Column(precision = 14, scale = 2)
  public BigDecimal defaultLateFine;

  @Convert(converter = FeeLineListConverter.class)
  @Column(columnDefinition = "text")
  public List<FeeLine> templateComponents = new ArrayList<>();

  @Convert(converter = ClassFeeScheduleListConverter.class)
  @Column(columnDefinition = "text")
  public List<ClassFeeSchedule> classSchedules = new ArrayList<>();

  public String createdDate;
  public String updatedDate;

  public FeeStructureEntity() {
  }
}

package com.travo.tcms.platform.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tcms_teaching_assignments")
public class TeachingAssignmentEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public Long id;

  public Long staffId;
  public String academicYear;
  public String campus;
  public String dutyType;
  public String className;
  public String sectionName;
  public String subject;
  public Integer weeklyPeriods;
  public Integer theoryPeriods;
  public Integer practicalPeriods;
  public Boolean classTeacher;
  public String effectiveFrom;
  public String effectiveTo;
  public String notes;
  public Integer weeklyCapacity;
  public String status;
}

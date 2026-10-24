package com.travo.tcms.platform.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tcms_curriculum_subjects")
public class CurriculumSubjectEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public Long id;

  public String academicYear;
  public String name;
  public String code;
  public String subjectType;
  public Integer displayOrder;
  public String status;
}

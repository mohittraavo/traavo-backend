package com.travo.tcms.platform.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tcms_class_sections")
public class ClassSectionEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public Long id;

  public String academicYear;
  public Long campusId;
  public Long classId;
  public String name;
  public String code;
  public Integer capacity;
  public String status;
  public String teacher;
}

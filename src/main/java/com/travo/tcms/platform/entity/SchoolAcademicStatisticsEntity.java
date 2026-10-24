package com.travo.tcms.platform.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "tcms_school_academic_statistics")
public class SchoolAcademicStatisticsEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public Long id;
  public Integer boys;
  public Integer girls;
  public Integer teachers;
  public Integer teachingStaff;
  public Integer nonTeachingStaff;
}

package com.travo.tcms.platform.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "tcms_school_academic_sessions")
public class SchoolAcademicSessionEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public Long id;
  public String academicSession;
  public LocalDate startDate;
  public LocalDate endDate;
}

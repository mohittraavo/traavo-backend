package com.travo.tcms.platform.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "tcms_school_management")
public class SchoolManagementEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public Long id;
  public String trustName;
  public String societyName;
  public String chairmanName;
  public String directorName;
  public String secretaryName;
  public String managementEmail;
  public String managementMobile;
  public String registrationNumber;
  public LocalDate registrationDate;
  public String gstNumber;
  public String panNumber;
}

package com.travo.tcms.platform.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "tcms_school_principals")
public class SchoolPrincipalEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public Long id;
  public String principalName;
  public String gender;
  public LocalDate dob;
  public String mobile;
  public String email;
  public String qualification;
  public Integer experience;
  public LocalDate joiningDate;
  public String aadhaarNumber;
  public String panNumber;
  public String photo;
}

package com.travo.tcms.platform.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tcms_school_profile")
public class SchoolProfileEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public Long id;

  public String schoolName;
  public String schoolCode;
  public String schoolType;
  public String contactPerson;
  public String contactEmail;
  public String telephone;
  public String website;
  public String postalAddress;
  public String country;
  public String state;
  public String city;
  public String postalCode;
  public String academicSession;
  public String schoolStatus;
}

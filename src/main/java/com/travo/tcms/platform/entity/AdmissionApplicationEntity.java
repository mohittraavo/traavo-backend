package com.travo.tcms.platform.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

@Entity
@Table(name = "tcms_admissions", indexes = {
    @Index(name = "idx_admissions_application_no", columnList = "application_no")
})
public class AdmissionApplicationEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public Long id;

  public String applicationNo;
  public String applicationDate;
  public String academicYear;
  public String campus;
  public String applyingClass;
  public String applyingSection;
  public String admissionSource;
  public String firstName;
  public String middleName;
  public String lastName;
  public String gender;
  public String dateOfBirth;
  public String bloodGroup;
  public String fatherName;
  public String motherName;
  public String guardianRelationship;
  public String phone;
  public String email;
  public String address;
  public String city;
  public String state;
  public String pincode;
  public String previousSchool;
  public String previousClass;
  public String previousGrade;
  public String entranceScore;
  public String remarks;
  public String documentType;
  public String documentNumber;
  public String verificationStatus;
  public String stage;
  public Long studentId;
}

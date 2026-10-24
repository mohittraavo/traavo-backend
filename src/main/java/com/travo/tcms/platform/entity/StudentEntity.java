package com.travo.tcms.platform.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "tcms_students", indexes = {
    @Index(name = "idx_students_admission_no", columnList = "admission_no"),
    @Index(name = "idx_students_class_section", columnList = "class_grade,section")
})
public class StudentEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public Long id;

  // Basic identifiers
  public String admissionNo;
  public String admissionDate;
  public String firstName;
  public String lastName;
  public String classGrade;
  public String section;
  public String rollNo;
  public String academicSession;

  // Personal
  public String dob;
  public String gender;
  public String bloodGroup;
  public String motherTongue;
  public String mediumOfInstruction;
  public String apaarId;
  public String udisePen;
  public String studentAadhaar;
  public String aadhaarStatus;
  public String udiseStudentCode;
  public String birthCertificateNo;
  public String birthCertificateAuthority;
  public String domicileState;
  public String domicileCertificateNo;
  public String admissionQuota;
  public String category;
  public String religion;
  public String minorityStatus;
  public String bplCardNo;
  public String familyAnnualIncome;

  // CWSN
  public String cwsnSpecialNeeds;
  public String disabilityType;
  public String disabilityPercentage;
  public String udidNo;
  public String specialAidsProvided;

  // Father
  public String fatherName;
  public String fatherMobile;
  public String fatherEmail;
  public String fatherQualification;
  public String fatherOccupation;
  public String fatherDesignation;
  public String fatherOrganization;
  public String fatherAnnualIncome;
  public String fatherAadhaar;
  public String fatherPAN;

  // Mother
  public String motherName;
  public String motherMobile;
  public String motherEmail;
  public String motherQualification;
  public String motherOccupation;
  public String motherDesignation;
  public String motherOrganization;
  public String motherAnnualIncome;
  public String motherAadhaar;
  public String motherPAN;

  // Guardian
  public String guardianName;
  public String guardianRelation;
  public String guardianPhone;
  public String guardianEmail;
  public String guardianOccupation;

  // Emergency
  public String emergencyDoctorName;
  public String emergencyDoctorPhone;
  public String preferredHospital;

  // Address
  public String residentialAddress;
  public String permanentAddress;
  public String city;
  public String state;
  public String pincode;
  public String distanceFromSchoolKm;

  // Previous
  public String previousSchoolName;
  public String previousBoard;
  public String previousClassPassed;
  public String tcNumber;
  public String tcDate;
  public String tcCounterSigned;

  // Transport
  public String transportRequired;
  public String transportMode;
  public String busRouteNo;
  public String busStopPickupPoint;
  public String morningPickupTime;
  public String afternoonDropTime;

  // Bank
  public String bankAccountHolderName;
  public String bankName;
  public String bankAccountNumber;
  public String bankIFSC;
  public String bankBranch;
  public String scholarshipAvailed;

  // Health
  public String heightCm;
  public String weightKg;
  public String bmi;
  public String visionLeft;
  public String visionRight;
  public String allergies;
  public String chronicConditions;
  public String vaccinationStatus;

  public String feeStatus;
  public String attendancePercent;

  // meta/raw
  @Column(columnDefinition = "text")
  public String rawDataJson;
}

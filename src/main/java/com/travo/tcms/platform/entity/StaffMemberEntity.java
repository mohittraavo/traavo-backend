package com.travo.tcms.platform.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

@Entity
@Table(name = "tcms_staff_members", indexes = {
    @Index(name = "idx_staff_employee_code", columnList = "employee_code")
})
public class StaffMemberEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public Long id;

  public String employeeCode;
  public String salutation;
  public String firstName;
  public String middleName;
  public String lastName;
  public String gender;
  public String dateOfBirth;
  public String bloodGroup;

  public String phone;
  public String email;
  public String address;
  public String city;
  public String state;
  public String pincode;

  public String aadhaar;
  public String pan;

  public String department;
  public String designation;
  public String category;
  public String workforce;
  public String campus;
  public String reportingManager;

  public String joiningDate;
  public String employmentType;
  public String status;

  public String qualification;
  public String experienceYears;
  public String emergencyName;
  public String emergencyPhone;
  public String bankAccount;
  public String ifsc;
}

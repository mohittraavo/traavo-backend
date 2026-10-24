package com.travo.tcms.platform.config;

import com.travo.tcms.platform.entity.AdmissionApplicationEntity;
import com.travo.tcms.platform.repository.AdmissionApplicationRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
public class AdmissionApplicationSeeder {
  private final AdmissionApplicationRepository repository;

  public AdmissionApplicationSeeder(AdmissionApplicationRepository repository) {
    this.repository = repository;
  }

  @PostConstruct
  void seed() {
    if (repository.count() > 0) return;
    add("APP-2026-104", "Samaira", "Chopra", "Female", "2016-06-18", "Grade 5", "Vikram Chopra", "+91 98100 88776", "95", "Admitted");
    add("APP-2026-103", "Ishaan", "Bose", "Male", "2015-12-05", "Grade 9", "Subrata Bose", "+91 94331 44556", "", "Submitted");
    add("APP-2026-102", "Tanvi", "Reddy", "Female", "2017-01-20", "Grade 3", "Venkat Reddy", "+91 99880 22334", "92", "Approved");
    add("APP-2026-101", "Pranav", "Kulkarni", "Male", "2013-08-11", "Grade 7", "Mahesh Kulkarni", "+91 98230 11998", "88", "Under Review");
  }

  private void add(String no, String first, String last, String gender, String dob, String grade, String parent, String phone, String score, String stage) {
    AdmissionApplicationEntity row = new AdmissionApplicationEntity();
    row.applicationNo = no;
    row.applicationDate = "2026-10-15";
    row.academicYear = "2026-2027";
    row.campus = "Main Campus";
    row.applyingClass = grade;
    row.applyingSection = "A";
    row.admissionSource = "Walk-in";
    row.firstName = first;
    row.lastName = last;
    row.gender = gender;
    row.dateOfBirth = dob;
    row.fatherName = parent;
    row.guardianRelationship = "Father";
    row.phone = phone;
    row.email = first.toLowerCase() + "." + last.toLowerCase() + "@example.com";
    row.address = "Delhi";
    row.city = "New Delhi";
    row.state = "Delhi";
    row.pincode = "110001";
    row.entranceScore = score;
    row.verificationStatus = "Pending";
    row.stage = stage;
    repository.save(row);
  }
}

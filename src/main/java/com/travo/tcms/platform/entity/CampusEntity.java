package com.travo.tcms.platform.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tcms_campuses")
public class CampusEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public Long id;

  public String externalId;
  public String schoolName;
  public String campusName;
  public String campusCode;
  public String city;
  public String status;
  public String address;
  public String state;
  public String pincode;
  public String latitude;
  public String longitude;
  public String principalName;
  public String principalMobile;
  public String principalEmail;
  public int maxStudents;
  public int currentStudents;
  public int maxTeachers;
  public int currentTeachers;
  public int buildings;
  public int classrooms;
  public String session;
  public int classes;
  public int sections;
  public String streams;
  public String subjects;
  public int students;
  public int teachers;
  public String attendance;
  public String feesCollection;
  public String pendingFees;
  public String openingTime;
  public String closingTime;
  public String weeklyOff;
  public String holidays;
  public String facilitiesCsv;
  public String permissionsCsv;
}

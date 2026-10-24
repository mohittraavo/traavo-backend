package com.travo.tcms.platform.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "tcms_school_medical")
public class SchoolMedicalEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public Long id;
  public Boolean medicalRoom;
  public Boolean doctorAvailable;
  public Boolean nurseAvailable;
  public Boolean ambulance;
  public String nearestHospital;
}

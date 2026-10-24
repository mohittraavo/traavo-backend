package com.travo.tcms.platform.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "tcms_school_vehicles")
public class SchoolVehicleEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public Long id;
  public String vehicleNumber;
  public String model;
  public Integer capacity;
}

package com.travo.tcms.platform.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "tcms_school_infrastructure")
public class SchoolInfrastructureEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public Long id;
  public Double campusArea;
  public Double buildingArea;
  public Integer smartClasses;
  public Integer computerLabs;
  public Integer scienceLabs;
  public Boolean libraryPresent;
  public Boolean playground;
  public Boolean auditorium;
  public Boolean hostel;
  public Boolean transport;
  public Boolean cctvInstalled;
  public Boolean biometricInstalled;
  public String fireSafetyCertificate;
  public Boolean drinkingWater;
  public Integer toiletsBoys;
  public Integer toiletsGirls;
  public Integer staffRooms;
  public Boolean medicalRoom;
}

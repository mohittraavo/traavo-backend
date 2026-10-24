package com.travo.tcms.platform.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "tcms_school_sports")
public class SchoolSportsEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public Long id;
  public String indoorGamesCsv;
  public String outdoorGamesCsv;
  public Boolean swimmingPool;
  public Boolean gymnasium;
  public Boolean coachAvailable;
}

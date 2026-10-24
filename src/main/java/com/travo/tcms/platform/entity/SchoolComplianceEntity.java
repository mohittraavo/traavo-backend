package com.travo.tcms.platform.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "tcms_school_compliance")
public class SchoolComplianceEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public Long id;
  public String childSafetyPolicy;
  public String poshPolicy;
  public String antiRaggingPolicy;
  public String disasterManagementPlan;
  public String insuranceDetails;
}

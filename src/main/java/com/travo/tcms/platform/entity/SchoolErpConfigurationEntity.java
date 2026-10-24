package com.travo.tcms.platform.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "tcms_school_erp_configuration")
public class SchoolErpConfigurationEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public Long id;
  public Boolean erpEnabled;
  public Boolean lmsEnabled;
  public Boolean attendanceModule;
  public Boolean transportModule;
  public Boolean hostelModule;
  public Boolean libraryModule;
  public Boolean hrModule;
  public Boolean payrollModule;
  public Boolean financeModule;
  public Boolean parentApp;
  public Boolean studentApp;
  public Boolean teacherApp;
}

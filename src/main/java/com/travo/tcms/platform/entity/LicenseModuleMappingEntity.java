package com.travo.tcms.platform.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "tcms_license_module_mapping")
public class LicenseModuleMappingEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public Long id;
  public String licenseCode;
  public String moduleName;
  public boolean isEnabled;
  public boolean readOnly;
  public Integer maxUsers;
  public Integer maxRecords;
  public boolean apiAccess;
  public boolean mobileApp;
  public Instant updatedAt;
}

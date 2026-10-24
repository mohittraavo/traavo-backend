package com.travo.tcms.platform.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "tcms_license_master")
public class LicenseMasterEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public Long id;
  public String licenseCode;
  public String licenseName;
  public String licenseType;
  public String subscriptionType;
  public String description;
  public int maxStudents;
  public int maxEmployees;
  public int maxCampuses;
  public int maxAdminUsers;
  public BigDecimal maxStorageGb;
  public Integer maxSmsCredits;
  public Integer maxEmailCredits;
  public Integer maxWhatsAppCredits;
  public BigDecimal price;
  public String currency;
  public boolean gstApplicable;
  public BigDecimal gstPercentage;
  public String status;
  public LocalDate effectiveFrom;
  public LocalDate effectiveTo;
  public boolean isRenewable;
  public Boolean autoRenew;
  public Instant createdAt;
  public Instant updatedAt;
}

package com.travo.tcms.platform.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "tcms_license_usage")
public class LicenseUsageEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public Long id;
  public Long assignmentId;
  public int currentStudents;
  public int currentStaff;
  public int currentCampuses;
  public BigDecimal currentStorageGb;
  public int smsUsed;
  public int emailUsed;
  public int whatsAppUsed;
  public Instant updatedAt;
}

package com.travo.tcms.platform.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "tcms_license_audit")
public class LicenseAuditEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public Long id;
  public Long assignmentId;
  public String eventType;
  public String eventStatus;
  public String message;
  public String performedBy;
  public Instant createdAt;
}

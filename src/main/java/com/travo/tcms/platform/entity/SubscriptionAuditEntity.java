package com.travo.tcms.platform.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "tcms_subscription_audit")
public class SubscriptionAuditEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public Long id;
  public String entityType;
  public String entityExternalId;
  public String action;
  public String schoolExternalId;
  public String performedBy;
  @Column(columnDefinition = "TEXT") public String oldValue;
  @Column(columnDefinition = "TEXT") public String newValue;
  public Instant createdAt;
}

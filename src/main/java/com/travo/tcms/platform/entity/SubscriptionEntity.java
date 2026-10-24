package com.travo.tcms.platform.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "tcms_subscriptions")
public class SubscriptionEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public Long id;
  public String externalId;
  public String schoolExternalId;
  public String schoolName;
  public String planExternalId;
  public String planName;
  public String status;
  public String billingCycle;
  public LocalDate startDate;
  public LocalDate endDate;
  public int gracePeriodDays;
  public LocalDate gracePeriodEndDate;
  public int price;
  public int studentLimit;
  public int campusLimit;
  @Column(columnDefinition = "TEXT") public String modulesJson;
  @Column(columnDefinition = "TEXT") public String featuresJson;
  @Column(columnDefinition = "TEXT") public String usageJson;
  public Instant createdAt;
  public Instant updatedAt;
}

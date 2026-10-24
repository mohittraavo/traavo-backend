package com.travo.tcms.platform.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "tcms_subscription_plans")
public class SubscriptionPlanEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public Long id;
  public String externalId;
  public String code;
  public String name;
  public String description;
  public String type;
  public String status;
  public String billingCycle;
  public int price;
  public Integer offerPrice;
  public String currency;
  public boolean trialEnabled;
  public Integer trialDuration;
  public int studentLimit;
  public int campusLimit;
  @Column(columnDefinition = "TEXT") public String modulesJson;
  @Column(columnDefinition = "TEXT") public String featuresJson;
  @Column(columnDefinition = "TEXT") public String limitsJson;
  public Instant createdAt;
  public Instant updatedAt;
}

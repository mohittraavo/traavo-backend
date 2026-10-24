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
@Table(name = "tcms_school_license_assignment")
public class SchoolLicenseAssignmentEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public Long id;
  public String schoolExternalId;
  public String schoolName;
  public String licenseCode;
  public String licenseName;
  public String licenseKey;
  public String tenantId;
  public String subscriptionNumber;
  public String purchaseOrderNo;
  public String invoiceNumber;
  public LocalDate startDate;
  public LocalDate expiryDate;
  public LocalDate activationDate;
  public LocalDate renewalDate;
  public String status;
  public boolean trial;
  public LocalDate trialEndDate;
  public Boolean autoRenewal;
  public Integer renewalReminderDays;
  public String paymentStatus;
  public BigDecimal amountPaid;
  public BigDecimal discount;
  public BigDecimal taxAmount;
  public BigDecimal netAmount;
  public String assignedBy;
  public Instant assignedDate;
  public String remarks;
}

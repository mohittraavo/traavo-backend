package com.travo.tcms.platform.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public record SchoolLicenseAssignmentRecord(
    Long assignmentId,
    String schoolId,
    String schoolName,
    String licenseCode,
    String licenseName,
    String licenseKey,
    String tenantId,
    String subscriptionNumber,
    String purchaseOrderNo,
    String invoiceNumber,
    LocalDate startDate,
    LocalDate expiryDate,
    LocalDate activationDate,
    LocalDate renewalDate,
    String status,
    boolean trial,
    LocalDate trialEndDate,
    Boolean autoRenewal,
    Integer renewalReminderDays,
    String paymentStatus,
    BigDecimal amountPaid,
    BigDecimal discount,
    BigDecimal taxAmount,
    BigDecimal netAmount,
    String assignedBy,
    Instant assignedDate,
    String remarks) {
}

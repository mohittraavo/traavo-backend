package com.travo.tcms.platform.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public record SchoolLicenseAssignmentCreateRequest(
    @NotBlank String schoolId,
    @NotBlank String licenseCode,
    @NotBlank String licenseKey,
    @NotBlank String tenantId,
    @NotBlank String subscriptionNumber,
    String purchaseOrderNo,
    String invoiceNumber,
    @NotNull LocalDate startDate,
    @NotNull LocalDate expiryDate,
    LocalDate activationDate,
    @NotBlank String status,
    boolean trial,
    LocalDate trialEndDate,
    Boolean autoRenewal,
    Integer renewalReminderDays,
    @NotBlank String paymentStatus,
    BigDecimal amountPaid,
    BigDecimal discount,
    String remarks) {
}

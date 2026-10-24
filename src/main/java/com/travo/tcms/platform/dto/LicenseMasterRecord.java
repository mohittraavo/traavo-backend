package com.travo.tcms.platform.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public record LicenseMasterRecord(
    Long licenseId,
    String licenseCode,
    String licenseName,
    String licenseType,
    String subscriptionType,
    String description,
    int maxStudents,
    int maxEmployees,
    int maxCampuses,
    int maxAdminUsers,
    BigDecimal maxStorageGb,
    Integer maxSmsCredits,
    Integer maxEmailCredits,
    Integer maxWhatsAppCredits,
    BigDecimal price,
    String currency,
    boolean gstApplicable,
    BigDecimal gstPercentage,
    String status,
    LocalDate effectiveFrom,
    LocalDate effectiveTo,
    boolean isRenewable,
    Boolean autoRenew,
    Instant createdAt,
    Instant updatedAt) {
}

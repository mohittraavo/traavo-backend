package com.travo.tcms.platform.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

public record LicenseMasterCreateRequest(
    @NotBlank String licenseCode,
    @NotBlank String licenseName,
    @NotBlank String licenseType,
    @NotBlank String subscriptionType,
    @Size(max = 1000) String description,
    @Min(0) int maxStudents,
    @Min(0) int maxEmployees,
    @Min(1) int maxCampuses,
    @Min(1) int maxAdminUsers,
    @DecimalMin(value = "0.0001") BigDecimal maxStorageGb,
    @Min(0) Integer maxSmsCredits,
    @Min(0) Integer maxEmailCredits,
    @Min(0) Integer maxWhatsAppCredits,
    @DecimalMin(value = "0.0") BigDecimal price,
    @NotBlank String currency,
    boolean gstApplicable,
    BigDecimal gstPercentage,
    @NotBlank String status,
    @NotNull LocalDate effectiveFrom,
    LocalDate effectiveTo,
    boolean isRenewable,
    Boolean autoRenew) {
}

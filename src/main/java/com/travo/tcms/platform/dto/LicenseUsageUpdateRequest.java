package com.travo.tcms.platform.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import java.math.BigDecimal;

public record LicenseUsageUpdateRequest(
    @Min(0) int currentStudents,
    @Min(0) int currentStaff,
    @Min(0) int currentCampuses,
    @DecimalMin(value = "0.0") BigDecimal currentStorageGb,
    @Min(0) int smsUsed,
    @Min(0) int emailUsed,
    @Min(0) int whatsAppUsed) {
}

package com.travo.tcms.platform.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record LicenseUsageRecord(
    Long assignmentId,
    int currentStudents,
    int maxStudents,
    int currentStaff,
    int maxStaff,
    int currentCampuses,
    int maxCampuses,
    BigDecimal currentStorageGb,
    BigDecimal maxStorageGb,
    int smsUsed,
    int smsBalance,
    int emailUsed,
    int emailBalance,
    int whatsAppUsed,
    int whatsAppBalance,
    Instant updatedAt) {
}

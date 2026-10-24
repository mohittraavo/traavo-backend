package com.travo.tcms.platform.dto;

import java.time.Instant;
import java.time.LocalDate;

public record LicenseRecord(String code, String schoolName, String board, String academicYear, int campusCount,
                            int studentCapacity, String contactName, String contactEmail, String status,
                            LocalDate validFrom, LocalDate validTo, Long activatedTenantId, Instant createdAt) {
}

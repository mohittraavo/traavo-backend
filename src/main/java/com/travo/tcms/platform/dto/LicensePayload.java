package com.travo.tcms.platform.dto;
import java.time.LocalDate;

public record LicensePayload(
        int version,
        String licenseId,
        String schoolName,
        String licenseType,
        String board,
        String academicYear,
        int campusCount,
        int studentCapacity,
        String contactName,
        String contactEmail,
        LocalDate validFrom,
        LocalDate validTo
) {}

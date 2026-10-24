package com.travo.tcms.platform.dto;

public record LicenseCreateRequest(
        String schoolName,
        String licenseType,
        String board,
        String academicYear,
        int campusCount,
        int studentCapacity,
        String contactName,
        String contactEmail
) {}
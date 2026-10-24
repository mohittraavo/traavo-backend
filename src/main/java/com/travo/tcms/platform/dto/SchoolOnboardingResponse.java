package com.travo.tcms.platform.dto;

public record SchoolOnboardingResponse(
    String id,
    String schoolCode,
    String schoolName,
    String status,
    String message
) {}

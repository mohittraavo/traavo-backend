package com.travo.tcms.platform.dto;

public record ComplianceDto(
    String childSafetyPolicy,
    String poshPolicy,
    String antiRaggingPolicy,
    String disasterManagementPlan,
    String insuranceDetails
) {}

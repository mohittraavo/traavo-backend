package com.travo.tcms.platform.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record LicenseModuleMappingUpsertRequest(
    @NotBlank String licenseCode,
    @NotBlank String module,
    boolean isEnabled,
    boolean readOnly,
    @Min(0) Integer maxUsers,
    @Min(0) Integer maxRecords,
    boolean apiAccess,
    boolean mobileApp) {
}

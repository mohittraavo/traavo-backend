package com.travo.tcms.platform.dto;

import jakarta.validation.constraints.NotBlank;

public record LicenseFeatureMappingUpsertRequest(
    @NotBlank String licenseCode,
    @NotBlank String feature,
    boolean enabled) {
}

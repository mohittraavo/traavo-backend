package com.travo.tcms.platform.dto;

import java.time.Instant;

public record LicenseFeatureMappingRecord(
    Long featureId,
    String licenseCode,
    String feature,
    boolean enabled,
    Instant updatedAt) {
}

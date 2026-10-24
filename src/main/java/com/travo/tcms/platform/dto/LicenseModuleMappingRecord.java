package com.travo.tcms.platform.dto;

import java.time.Instant;

public record LicenseModuleMappingRecord(
    Long mappingId,
    String licenseCode,
    String module,
    boolean isEnabled,
    boolean readOnly,
    Integer maxUsers,
    Integer maxRecords,
    boolean apiAccess,
    boolean mobileApp,
    Instant updatedAt) {
}

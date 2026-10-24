package com.travo.tcms.platform.dto;

import java.time.LocalDate;
import java.util.List;

public record LicenseInfo(String licenseKey, String licenseType, LocalDate activeUntil, LocalDate renewalDate,
                           List<String> history) {
}

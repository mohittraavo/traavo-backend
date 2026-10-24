package com.travo.tcms.platform.dto;

import java.time.Instant;

public record UserRecord(Long userId, Long tenantId, String fullName, String email, String role, boolean active,
                         Instant createdAt) {
}

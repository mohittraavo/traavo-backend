package com.travo.tcms.platform.dto;

import java.time.Instant;

public record LicenseAuditRecord(
    Long auditId,
    Long assignmentId,
    String eventType,
    String eventStatus,
    String message,
    String performedBy,
    Instant createdAt) {
}

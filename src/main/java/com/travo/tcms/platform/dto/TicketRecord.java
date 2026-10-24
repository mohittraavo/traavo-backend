package com.travo.tcms.platform.dto;

import java.time.Instant;

public record TicketRecord(Long ticketId, Long tenantId, String campusName, String subject, String description,
                           String priority, String status, String resolutionNote, Instant createdAt, Instant updatedAt) {
}

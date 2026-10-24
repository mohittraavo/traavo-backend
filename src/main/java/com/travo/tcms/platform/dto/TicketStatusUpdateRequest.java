package com.travo.tcms.platform.dto;

import jakarta.validation.constraints.NotBlank;

public record TicketStatusUpdateRequest(@NotBlank String status, String resolutionNote) {
}

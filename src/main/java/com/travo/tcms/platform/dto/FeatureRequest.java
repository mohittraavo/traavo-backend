package com.travo.tcms.platform.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record FeatureRequest(
    @NotBlank String name,
    @NotBlank String category,
    String description,
    @Min(0) int price,
    @NotBlank String billingCycle,
    @NotBlank String status) {
}

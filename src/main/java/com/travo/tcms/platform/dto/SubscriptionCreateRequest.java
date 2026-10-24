package com.travo.tcms.platform.dto;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.util.List;

public record SubscriptionCreateRequest(
    @NotBlank String planId,
    String billingCycle,
    LocalDate startDate,
    LocalDate endDate,
    Integer studentLimit,
    Integer campusLimit,
    List<String> featureFlags
) {}

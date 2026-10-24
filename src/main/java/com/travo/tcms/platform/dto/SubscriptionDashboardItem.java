package com.travo.tcms.platform.dto;

import java.time.LocalDate;

public record SubscriptionDashboardItem(
    String subscriptionId,
    String schoolId,
    String schoolName,
    String planName,
    String status,
    LocalDate endDate,
    int gracePeriodDays,
    LocalDate gracePeriodEndDate,
    long daysRemaining) {
}

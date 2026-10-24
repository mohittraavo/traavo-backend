package com.travo.tcms.platform.dto;

import java.util.List;

public record DashboardSummary(long tenantCount, long userCount, long generatedLicenseCount, long activeLicenseCount,
                                long openTicketCount, List<String> baselineRoles,
                                long activeSubscriptionCount, long expiringSubscriptionCount,
                                long gracePeriodSubscriptionCount, List<SubscriptionDashboardItem> subscriptionAlerts) {
}

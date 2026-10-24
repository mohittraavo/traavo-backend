package com.travo.tcms.platform.dto;

import java.time.LocalDate;
import java.util.List;

public record SubscriptionInfo(
    String currentPlan,
    LocalDate startDate,
    LocalDate expiryDate,
    String billingCycle,
    String paymentStatus,
    int studentLimit,
    int campusLimit,
    List<String> featureFlags
) {
  public SubscriptionInfo(
      String currentPlan,
      LocalDate startDate,
      LocalDate expiryDate,
      String billingCycle,
      String paymentStatus,
      int studentLimit,
      int campusLimit,
      List<String> featureFlags,
      String status
  ) {
    this(currentPlan, startDate, expiryDate, billingCycle, paymentStatus, studentLimit, campusLimit, featureFlags);
  }
}

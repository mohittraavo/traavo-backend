package com.travo.tcms.platform.dto;

import java.util.List;

public record SubscriptionPlanRecord(
    String id,
    String name,
    int price,
    Integer offerPrice,
    String billingCycle,
    int studentLimit,
    int campusLimit,
    List<String> features
) {
  public SubscriptionPlanRecord(
      String id,
      String name,
      String code,
      String description,
      String type,
      String status,
      int price,
      Integer offerPrice,
      String billingCycle,
      int studentLimit,
      int campusLimit,
      List<String> modules,
      List<String> features
  ) {
    this(id, name, price, offerPrice, billingCycle, studentLimit, campusLimit, features);
  }
}

package com.travo.tcms.platform.dto;

public record FeatureRecord(
    String id,
    String name,
    String category,
    String description,
    int price,
    String billingCycle,
    String status) {
}

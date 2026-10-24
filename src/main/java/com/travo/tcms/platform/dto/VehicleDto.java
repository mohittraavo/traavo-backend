package com.travo.tcms.platform.dto;

public record VehicleDto(
    String registrationNumber,
    String vehicleType,
    Integer capacity
) {}

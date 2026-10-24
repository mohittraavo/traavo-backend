package com.travo.tcms.platform.dto;

import java.util.List;

public record TransportDto(
    Boolean transportAvailable,
    Integer numberOfBuses,
    Boolean gpsEnabled,
    List<DriverDto> drivers,
    List<RouteDto> routes,
    List<VehicleDto> vehicles
) {}

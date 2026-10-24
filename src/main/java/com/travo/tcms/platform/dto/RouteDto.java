package com.travo.tcms.platform.dto;

import java.util.List;

public record RouteDto(
    String routeName,
    String startPoint,
    String endPoint,
    List<String> stops
) {}

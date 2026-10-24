package com.travo.tcms.platform.dto;

import java.util.List;

public record SportsDto(
    List<String> indoorGames,
    List<String> outdoorGames,
    Boolean swimmingPool,
    Boolean gymnasium,
    Boolean coachAvailable
) {}

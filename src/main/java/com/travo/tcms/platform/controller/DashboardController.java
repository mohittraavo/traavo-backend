package com.travo.tcms.platform.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.travo.tcms.platform.dto.DashboardSummary;
import com.travo.tcms.platform.service.DashboardService;

@RestController
@RequestMapping("/admin")
public class DashboardController {
  private final DashboardService dashboardService;

  public DashboardController(DashboardService dashboardService) {
    this.dashboardService = dashboardService;
  }

  @GetMapping("/dashboard")
  public DashboardSummary dashboard() {
    return dashboardService.getDashboardSummary();
  }
}

package com.travo.tcms.platform.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.travo.tcms.platform.service.SchoolService;

@RestController
@RequestMapping("/maintenance")
public class MaintenanceController {

  private final SchoolService schoolService;

  public MaintenanceController(SchoolService schoolService) {
    this.schoolService = schoolService;
  }

  @PostMapping("/licenses/clear-all")
  public java.util.Map<String, Object> clearAllLicenses() {
    int cleared = schoolService.clearAllSchoolLicenses();
    return java.util.Map.of("cleared", cleared);
  }
}

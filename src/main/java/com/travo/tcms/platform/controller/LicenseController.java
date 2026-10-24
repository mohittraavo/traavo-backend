package com.travo.tcms.platform.controller;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.travo.tcms.platform.dto.*;
import com.travo.tcms.platform.service.LicenseService;
import com.travo.tcms.platform.service.LicenseAdminService;

@RestController
@RequestMapping("/admin/licenses")
public class LicenseController {
  private final LicenseService licenseService;
  private final LicenseAdminService licenseAdminService;

  public LicenseController(LicenseService licenseService, LicenseAdminService licenseAdminService) {
    this.licenseService = licenseService;
    this.licenseAdminService = licenseAdminService;
  }

  @GetMapping("")
  public List<LicenseRecord> listLicenses() {
    return licenseService.listLicenses();
  }

  @PostMapping("")
  public LicenseRecord createLicense(@Valid @RequestBody LicenseCreateRequest request) {
    return licenseService.createLicense(request);
  }

  @GetMapping("/master")
  public List<LicenseMasterRecord> listLicenseMasters() {
    return licenseAdminService.listLicenseMasters();
  }

  @PostMapping("/master")
  public LicenseMasterRecord createLicenseMaster(@Valid @RequestBody LicenseMasterCreateRequest request) {
    return licenseAdminService.createLicenseMaster(request);
  }

  @GetMapping("/assignments")
  public List<SchoolLicenseAssignmentRecord> listAssignments() {
    return licenseAdminService.listSchoolLicenseAssignments();
  }

  @PostMapping("/assignments")
  public SchoolLicenseAssignmentRecord assignLicense(@Valid @RequestBody SchoolLicenseAssignmentCreateRequest request) {
    return licenseAdminService.assignLicenseToSchool(request);
  }

  @GetMapping("/module-mappings/{licenseCode}")
  public List<LicenseModuleMappingRecord> listModuleMappings(@PathVariable String licenseCode) {
    return licenseAdminService.listModuleMappings(licenseCode);
  }

  @PostMapping("/module-mappings")
  public LicenseModuleMappingRecord upsertModuleMapping(@Valid @RequestBody LicenseModuleMappingUpsertRequest request) {
    return licenseAdminService.upsertModuleMapping(request);
  }

  @GetMapping("/feature-mappings/{licenseCode}")
  public List<LicenseFeatureMappingRecord> listFeatureMappings(@PathVariable String licenseCode) {
    return licenseAdminService.listFeatureMappings(licenseCode);
  }

  @PostMapping("/feature-mappings")
  public LicenseFeatureMappingRecord upsertFeatureMapping(@Valid @RequestBody LicenseFeatureMappingUpsertRequest request) {
    return licenseAdminService.upsertFeatureMapping(request);
  }

  @GetMapping("/usage/{assignmentId}")
  public LicenseUsageRecord getUsage(@PathVariable Long assignmentId) {
    return licenseAdminService.getUsage(assignmentId);
  }

  @PutMapping("/usage/{assignmentId}")
  public LicenseUsageRecord updateUsage(@PathVariable Long assignmentId,
                                        @Valid @RequestBody LicenseUsageUpdateRequest request) {
    return licenseAdminService.updateUsage(assignmentId, request);
  }

  @GetMapping("/audit/{assignmentId}")
  public List<LicenseAuditRecord> listLicenseAudits(@PathVariable Long assignmentId) {
    return licenseAdminService.listLicenseAudits(assignmentId);
  }

  @PostMapping("/{licenseCode}/send")
  public LicenseRecord sendLicense(@PathVariable String licenseCode) {
    return licenseService.sendLicense(licenseCode);
  }
}

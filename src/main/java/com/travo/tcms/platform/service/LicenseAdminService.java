package com.travo.tcms.platform.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import com.travo.tcms.platform.dto.*;
import com.travo.tcms.platform.entity.*;
import com.travo.tcms.platform.repository.*;

@Service
public class LicenseAdminService {

  private final LicenseMasterRepository licenseMasterRepository;
  private final SchoolLicenseAssignmentRepository schoolLicenseAssignmentRepository;
  private final LicenseModuleMappingRepository licenseModuleMappingRepository;
  private final LicenseFeatureMappingRepository licenseFeatureMappingRepository;
  private final LicenseUsageRepository licenseUsageRepository;
  private final LicenseAuditRepository licenseAuditRepository;
  private final LicenseRepository licenseRepository;
  private final SchoolRepository schoolRepository;

  public LicenseAdminService(LicenseMasterRepository licenseMasterRepository,
      SchoolLicenseAssignmentRepository schoolLicenseAssignmentRepository,
      LicenseModuleMappingRepository licenseModuleMappingRepository,
      LicenseFeatureMappingRepository licenseFeatureMappingRepository,
      LicenseUsageRepository licenseUsageRepository, LicenseAuditRepository licenseAuditRepository,
      LicenseRepository licenseRepository, SchoolRepository schoolRepository) {
    this.licenseMasterRepository = licenseMasterRepository;
    this.schoolLicenseAssignmentRepository = schoolLicenseAssignmentRepository;
    this.licenseModuleMappingRepository = licenseModuleMappingRepository;
    this.licenseFeatureMappingRepository = licenseFeatureMappingRepository;
    this.licenseUsageRepository = licenseUsageRepository;
    this.licenseAuditRepository = licenseAuditRepository;
    this.licenseRepository = licenseRepository;
    this.schoolRepository = schoolRepository;
  }

  public List<LicenseMasterRecord> listLicenseMasters() {
    return licenseMasterRepository.findAllByOrderByCreatedAtDesc().stream().map(entity ->
        new LicenseMasterRecord(
            entity.id,
            entity.licenseCode,
            entity.licenseName,
            entity.licenseType,
            entity.subscriptionType,
            entity.description,
            entity.maxStudents,
            entity.maxEmployees,
            entity.maxCampuses,
            entity.maxAdminUsers,
            entity.maxStorageGb,
            entity.maxSmsCredits,
            entity.maxEmailCredits,
            entity.maxWhatsAppCredits,
            entity.price,
            entity.currency,
            entity.gstApplicable,
            entity.gstPercentage,
            entity.status,
            entity.effectiveFrom,
            entity.effectiveTo,
            entity.isRenewable,
            entity.autoRenew,
            entity.createdAt,
            entity.updatedAt))
        .toList();
  }

  public LicenseMasterRecord createLicenseMaster(LicenseMasterCreateRequest request) {
    if (licenseMasterRepository.existsByLicenseCodeIgnoreCase(request.licenseCode())) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "License code already exists");
    }
    if (licenseMasterRepository.existsByLicenseNameIgnoreCase(request.licenseName())) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "License name already exists");
    }
    LicenseMasterEntity entity = new LicenseMasterEntity();
    entity.licenseCode = request.licenseCode().trim();
    entity.licenseName = request.licenseName().trim();
    entity.licenseType = request.licenseType().trim();
    entity.subscriptionType = request.subscriptionType().trim();
    entity.description = request.description();
    entity.maxStudents = request.maxStudents();
    entity.maxEmployees = request.maxEmployees();
    entity.maxCampuses = request.maxCampuses();
    entity.maxAdminUsers = request.maxAdminUsers();
    entity.maxStorageGb = request.maxStorageGb();
    entity.maxSmsCredits = request.maxSmsCredits() == null ? 0 : request.maxSmsCredits();
    entity.maxEmailCredits = request.maxEmailCredits() == null ? 0 : request.maxEmailCredits();
    entity.maxWhatsAppCredits = request.maxWhatsAppCredits() == null ? 0 : request.maxWhatsAppCredits();
    entity.price = request.price();
    entity.currency = request.currency().trim();
    entity.gstApplicable = request.gstApplicable();
    entity.gstPercentage = request.gstApplicable() ? (request.gstPercentage() == null ? BigDecimal.valueOf(18) : request.gstPercentage()) : BigDecimal.ZERO;
    entity.status = request.status().trim();
    entity.effectiveFrom = request.effectiveFrom();
    entity.effectiveTo = request.effectiveTo();
    entity.isRenewable = request.isRenewable();
    entity.autoRenew = request.autoRenew();
    entity.createdAt = Instant.now();
    entity.updatedAt = Instant.now();
    licenseMasterRepository.save(entity);
    return new LicenseMasterRecord(
        entity.id,
        entity.licenseCode,
        entity.licenseName,
        entity.licenseType,
        entity.subscriptionType,
        entity.description,
        entity.maxStudents,
        entity.maxEmployees,
        entity.maxCampuses,
        entity.maxAdminUsers,
        entity.maxStorageGb,
        entity.maxSmsCredits,
        entity.maxEmailCredits,
        entity.maxWhatsAppCredits,
        entity.price,
        entity.currency,
        entity.gstApplicable,
        entity.gstPercentage,
        entity.status,
        entity.effectiveFrom,
        entity.effectiveTo,
        entity.isRenewable,
        entity.autoRenew,
        entity.createdAt,
        entity.updatedAt);
  }

  public List<SchoolLicenseAssignmentRecord> listSchoolLicenseAssignments() {
    return schoolLicenseAssignmentRepository.findAllByOrderByAssignedDateDesc().stream()
        .map(entity -> new SchoolLicenseAssignmentRecord(
            entity.id,
            entity.schoolExternalId,
            entity.schoolName,
            entity.licenseCode,
            entity.licenseName,
            entity.licenseKey,
            entity.tenantId,
            entity.subscriptionNumber,
            entity.purchaseOrderNo,
            entity.invoiceNumber,
            entity.startDate,
            entity.expiryDate,
            entity.activationDate,
            entity.renewalDate,
            entity.status,
            entity.trial,
            entity.trialEndDate,
            entity.autoRenewal,
            entity.renewalReminderDays,
            entity.paymentStatus,
            entity.amountPaid,
            entity.discount,
            entity.taxAmount,
            entity.netAmount,
            entity.assignedBy,
            entity.assignedDate,
            entity.remarks))
        .toList();
  }

  public SchoolLicenseAssignmentRecord assignLicenseToSchool(SchoolLicenseAssignmentCreateRequest request) {
    SchoolEntity school = schoolRepository.findByExternalId(request.schoolId())
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "School does not exist"));
    LicenseMasterEntity license = licenseMasterRepository.findByLicenseCodeIgnoreCase(request.licenseCode())
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "License does not exist"));
    if (schoolLicenseAssignmentRepository.existsByLicenseKeyIgnoreCase(request.licenseKey())) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "License key already exists");
    }
    if (schoolLicenseAssignmentRepository.existsByTenantIdIgnoreCase(request.tenantId())) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Tenant ID already exists");
    }
    SchoolLicenseAssignmentEntity entity = new SchoolLicenseAssignmentEntity();
    entity.schoolExternalId = school.externalId;
    entity.schoolName = school.schoolName;
    entity.licenseCode = license.licenseCode;
    entity.licenseName = license.licenseName;
    entity.licenseKey = request.licenseKey().trim();
    entity.tenantId = request.tenantId().trim();
    entity.subscriptionNumber = request.subscriptionNumber().trim();
    entity.startDate = request.startDate();
    entity.expiryDate = request.expiryDate();
    entity.activationDate = request.activationDate();
    entity.renewalReminderDays = request.renewalReminderDays() == null ? 30 : request.renewalReminderDays();
    entity.renewalDate = request.expiryDate() == null ? null : request.expiryDate().minusDays(entity.renewalReminderDays);
    entity.status = request.status().trim();
    entity.trial = request.trial();
    entity.trialEndDate = request.trialEndDate();
    entity.autoRenewal = request.autoRenewal();
    entity.paymentStatus = request.paymentStatus() == null ? "" : request.paymentStatus().trim();
    entity.amountPaid = request.amountPaid() == null ? BigDecimal.ZERO : request.amountPaid();
    entity.discount = request.discount() == null ? BigDecimal.ZERO : request.discount();
    entity.assignedBy = "SYSTEM";
    entity.assignedDate = Instant.now();
    entity.remarks = request.remarks();
    schoolLicenseAssignmentRepository.save(entity);
    return new SchoolLicenseAssignmentRecord(
        entity.id,
        entity.schoolExternalId,
        entity.schoolName,
        entity.licenseCode,
        entity.licenseName,
        entity.licenseKey,
        entity.tenantId,
        entity.subscriptionNumber,
        entity.purchaseOrderNo,
        entity.invoiceNumber,
        entity.startDate,
        entity.expiryDate,
        entity.activationDate,
        entity.renewalDate,
        entity.status,
        entity.trial,
        entity.trialEndDate,
        entity.autoRenewal,
        entity.renewalReminderDays,
        entity.paymentStatus,
        entity.amountPaid,
        entity.discount,
        entity.taxAmount,
        entity.netAmount,
        entity.assignedBy,
        entity.assignedDate,
        entity.remarks);
  }

  public List<LicenseModuleMappingRecord> listModuleMappings(String licenseCode) {
    return licenseModuleMappingRepository.findByLicenseCodeIgnoreCaseOrderByModuleNameAsc(licenseCode).stream()
        .map(e -> new LicenseModuleMappingRecord(e.id, e.licenseCode, e.moduleName, e.isEnabled, e.readOnly, e.maxUsers,
            e.maxRecords, e.apiAccess, e.mobileApp, e.updatedAt)).toList();
  }

  public LicenseModuleMappingRecord upsertModuleMapping(LicenseModuleMappingUpsertRequest request) {
    licenseMasterRepository.findByLicenseCodeIgnoreCase(request.licenseCode())
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "License does not exist"));
    LicenseModuleMappingEntity entity = licenseModuleMappingRepository
        .findByLicenseCodeIgnoreCaseAndModuleNameIgnoreCase(request.licenseCode(), request.module())
        .orElseGet(LicenseModuleMappingEntity::new);
    entity.licenseCode = request.licenseCode().trim();
    entity.moduleName = request.module().trim();
    entity.isEnabled = request.isEnabled();
    entity.readOnly = request.readOnly();
    entity.maxUsers = request.maxUsers();
    entity.maxRecords = request.maxRecords();
    entity.apiAccess = request.apiAccess();
    entity.mobileApp = request.mobileApp();
    entity.updatedAt = Instant.now();
    licenseModuleMappingRepository.save(entity);
    return new LicenseModuleMappingRecord(entity.id, entity.licenseCode, entity.moduleName, entity.isEnabled,
        entity.readOnly, entity.maxUsers, entity.maxRecords, entity.apiAccess, entity.mobileApp, entity.updatedAt);
  }

  public List<LicenseFeatureMappingRecord> listFeatureMappings(String licenseCode) {
    return licenseFeatureMappingRepository.findByLicenseCodeIgnoreCaseOrderByFeatureNameAsc(licenseCode).stream()
        .map(e -> new LicenseFeatureMappingRecord(e.id, e.licenseCode, e.featureName, e.enabled, e.updatedAt)).toList();
  }

  public LicenseFeatureMappingRecord upsertFeatureMapping(LicenseFeatureMappingUpsertRequest request) {
    licenseMasterRepository.findByLicenseCodeIgnoreCase(request.licenseCode())
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "License does not exist"));
    LicenseFeatureMappingEntity entity = licenseFeatureMappingRepository
        .findByLicenseCodeIgnoreCaseAndFeatureNameIgnoreCase(request.licenseCode(), request.feature())
        .orElseGet(LicenseFeatureMappingEntity::new);
    entity.licenseCode = request.licenseCode().trim();
    entity.featureName = request.feature().trim();
    entity.enabled = request.enabled();
    entity.updatedAt = Instant.now();
    licenseFeatureMappingRepository.save(entity);
    return new LicenseFeatureMappingRecord(entity.id, entity.licenseCode, entity.featureName, entity.enabled, entity.updatedAt);
  }

  public LicenseUsageRecord getUsage(Long assignmentId) {
    SchoolLicenseAssignmentEntity assignment = schoolLicenseAssignmentRepository.findById(assignmentId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Assignment not found"));
    LicenseMasterEntity license = licenseMasterRepository.findByLicenseCodeIgnoreCase(assignment.licenseCode)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "License not found"));
    LicenseUsageEntity usage = licenseUsageRepository.findByAssignmentId(assignmentId).orElseGet(() -> {
      LicenseUsageEntity entity = new LicenseUsageEntity();
      entity.assignmentId = assignmentId;
      entity.currentStudents = 0;
      entity.currentStaff = 0;
      entity.currentCampuses = 0;
      entity.currentStorageGb = BigDecimal.ZERO;
      entity.smsUsed = 0;
      entity.emailUsed = 0;
      entity.whatsAppUsed = 0;
      entity.updatedAt = Instant.now();
      return licenseUsageRepository.save(entity);
    });
    return toUsageRecord(assignment, license, usage);
  }

  public LicenseUsageRecord updateUsage(Long assignmentId, LicenseUsageUpdateRequest request) {
    SchoolLicenseAssignmentEntity assignment = schoolLicenseAssignmentRepository.findById(assignmentId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Assignment not found"));
    LicenseMasterEntity license = licenseMasterRepository.findByLicenseCodeIgnoreCase(assignment.licenseCode)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "License not found"));
    LicenseUsageEntity usage = licenseUsageRepository.findByAssignmentId(assignmentId).orElseGet(() -> {
      LicenseUsageEntity entity = new LicenseUsageEntity();
      entity.assignmentId = assignmentId;
      return entity;
    });
    usage.currentStudents = request.currentStudents();
    usage.currentStaff = request.currentStaff();
    usage.currentCampuses = request.currentCampuses();
    usage.currentStorageGb = request.currentStorageGb() == null ? BigDecimal.ZERO : request.currentStorageGb();
    usage.smsUsed = request.smsUsed();
    usage.emailUsed = request.emailUsed();
    usage.whatsAppUsed = request.whatsAppUsed();
    usage.updatedAt = Instant.now();
    licenseUsageRepository.save(usage);
    return toUsageRecord(assignment, license, usage);
  }

  private LicenseUsageRecord toUsageRecord(SchoolLicenseAssignmentEntity assignment, LicenseMasterEntity license, LicenseUsageEntity usage) {
    int maxSms = license.maxSmsCredits == null ? 0 : license.maxSmsCredits;
    int smsBalance = Math.max(0, maxSms - usage.smsUsed);
    int maxEmail = license.maxEmailCredits == null ? 0 : license.maxEmailCredits;
    int emailBalance = Math.max(0, maxEmail - usage.emailUsed);
    int maxWhatsapp = license.maxWhatsAppCredits == null ? 0 : license.maxWhatsAppCredits;
    int whatsAppBalance = Math.max(0, maxWhatsapp - usage.whatsAppUsed);
    return new LicenseUsageRecord(
        assignment.id,
        usage.currentStudents,
        license.maxStudents,
        usage.currentStaff,
        license.maxEmployees,
        usage.currentCampuses,
        license.maxCampuses,
        usage.currentStorageGb,
        license.maxStorageGb,
        usage.smsUsed,
        smsBalance,
        usage.emailUsed,
        emailBalance,
        usage.whatsAppUsed,
        whatsAppBalance,
        usage.updatedAt);
  }

  public List<LicenseAuditRecord> listLicenseAudits(Long assignmentId) {
    return licenseAuditRepository.findByAssignmentIdOrderByCreatedAtDesc(assignmentId).stream()
        .map(a -> new LicenseAuditRecord(a.id, a.assignmentId, a.eventType, a.eventStatus, a.message, a.performedBy, a.createdAt)).toList();
  }
}

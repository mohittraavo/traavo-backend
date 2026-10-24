package com.travo.tcms.platform.service;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import com.travo.tcms.platform.repository.LicenseRepository;
import com.travo.tcms.platform.entity.LicenseEntity;
import com.travo.tcms.platform.dto.LicenseRecord;
import com.travo.tcms.platform.dto.LicenseCreateRequest;

@Service
public class LicenseService {
  private final LicenseRepository licenseRepository;
  private final LicenseEmailService licenseEmailService;

  public LicenseService(LicenseRepository licenseRepository, LicenseEmailService licenseEmailService) {
    this.licenseRepository = licenseRepository;
    this.licenseEmailService = licenseEmailService;
  }

  public List<LicenseRecord> listLicenses() {
    return licenseRepository.findAllByOrderByCreatedAtDesc().stream().map(this::toLicenseRecord).toList();
  }

  public LicenseRecord createLicense(LicenseCreateRequest request) {
    LicenseEntity entity = new LicenseEntity();

    entity.code =
            "LIC-" + UUID.randomUUID()
                    .toString()
                    .substring(0, 8)
                    .toUpperCase();

    entity.schoolName = request.schoolName();
    entity.licenseType = request.licenseType();
    entity.board = request.board();
    entity.academicYear = request.academicYear();
    entity.campusCount = request.campusCount();
    entity.studentCapacity = request.studentCapacity();
    entity.contactName = request.contactName();
    entity.contactEmail = request.contactEmail();

    entity.status = "GENERATED";

    entity.validFrom = LocalDate.now();
    entity.validTo = LocalDate.now().plusMonths(12);

    entity.createdAt = Instant.now();

    licenseRepository.save(entity);

    return toLicenseRecord(entity);
  }

  public LicenseRecord sendLicense(String licenseCode) {
    LicenseEntity entity =
            licenseRepository.findAll()
                    .stream()
                    .filter(license -> license.code != null && license.code.equalsIgnoreCase(licenseCode))
                    .findFirst()
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "License not found"));

    if ("ACTIVATED".equalsIgnoreCase(entity.status)) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Activated license cannot be sent again");
    }
    if ("REVOKED".equalsIgnoreCase(entity.status)) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Revoked license cannot be sent");
    }
    if (entity.contactEmail == null || entity.contactEmail.isBlank()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "License does not have a contact email");
    }

    licenseEmailService.sendLicense(entity);
    entity.status = "SENT";
    licenseRepository.save(entity);
    return toLicenseRecord(entity);
  }

  private LicenseRecord toLicenseRecord(LicenseEntity entity) {
    return new LicenseRecord(entity.code, entity.schoolName, entity.board, entity.academicYear, entity.campusCount,
        entity.studentCapacity, entity.contactName, entity.contactEmail, entity.status, entity.validFrom,
        entity.validTo, entity.activatedTenantId, entity.createdAt);
  }
}

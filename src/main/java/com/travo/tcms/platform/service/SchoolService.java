package com.travo.tcms.platform.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.travo.tcms.platform.dto.*;
import com.travo.tcms.platform.entity.*;
import com.travo.tcms.platform.repository.*;
import com.travo.tcms.platform.mapper.SchoolMapper;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@Service
public class SchoolService {

  private final SchoolRepository schoolRepository;
  private final CampusRepository campusRepository;

  public SchoolService(SchoolRepository schoolRepository, CampusRepository campusRepository) {
    this.schoolRepository = schoolRepository;
    this.campusRepository = campusRepository;
  }

  public List<SchoolListRecord> listSchools() {
    return schoolRepository.findAllByOrderByCreatedDateDesc().stream().map(entity ->
        new SchoolListRecord(entity.externalId, entity.schoolName, entity.schoolCode, entity.board,
            entity.subscriptionPlan, entity.licenseStatus, entity.totalCampuses, entity.totalStudents,
          (entity.principal != null ? entity.principal.principalName : entity.schoolAdminName), entity.status, entity.createdDate)).toList();
  }

  @Transactional(readOnly = true)
  public SchoolDetailRecord getSchool(String externalId) {
    SchoolEntity entity = schoolRepository.findByExternalId(externalId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "School not found"));
    return SchoolMapper.toDto(entity);
  }

  public List<CampusListRecord> listCampuses() {
    return campusRepository.findAllByOrderByCampusNameAsc().stream().map(entity ->
        new CampusListRecord(entity.externalId, entity.campusName, entity.campusCode, entity.schoolName,
            entity.city, entity.principalName, entity.students, entity.teachers, entity.classes, entity.status)).toList();
  }

  public CampusDetailRecord getCampus(String externalId) {
    CampusEntity entity = campusRepository.findByExternalId(externalId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Campus not found"));
    return new CampusDetailRecord(
        entity.externalId,
        entity.schoolName,
        entity.campusName,
        entity.campusCode,
        entity.city,
        entity.status,
        new CampusBasicInfo(entity.address, entity.state, entity.city, entity.pincode, entity.latitude, entity.longitude),
        new CampusPrincipalInfo(entity.principalName, entity.principalMobile, entity.principalEmail),
        new CampusCapacityInfo(entity.maxStudents, entity.currentStudents, entity.maxTeachers, entity.currentTeachers,
            entity.buildings, entity.classrooms),
        new CampusAcademicInfo(entity.session, String.valueOf(entity.classes), entity.sections, entity.streams, entity.subjects),
        List.of(),
        new CampusStatisticsInfo(entity.students, entity.teachers, entity.attendance, entity.feesCollection, entity.pendingFees),
        new CampusTimingInfo(entity.openingTime, entity.closingTime, entity.weeklyOff, entity.holidays),
        List.of());
  }

  public List<CampusCapacityRecord> listCampusCapacity() {
    return campusRepository.findAllByOrderByCampusNameAsc().stream()
        .map(entity -> new CampusCapacityRecord(entity.externalId, entity.campusName, entity.schoolName,
            entity.maxStudents, entity.currentStudents, entity.maxTeachers, entity.currentTeachers))
        .toList();
  }

  public int clearAllSchoolLicenses() {
    var schools = schoolRepository.findAll();
    for (SchoolEntity s : schools) {
      s.licenseKey = null;
      s.licenseType = null;
      s.licenseHistoryCsv = null;
      s.licenseStatus = null;
      s.activeUntil = null;
      s.renewalDate = null;
    }
    schoolRepository.saveAll(schools);
    return schools.size();
  }
}

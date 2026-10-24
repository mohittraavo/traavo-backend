package com.travo.tcms.platform.controller;

import com.travo.tcms.platform.entity.AcademicClassEntity;
import com.travo.tcms.platform.entity.SchoolBranchEntity;
import com.travo.tcms.platform.entity.ClassSectionEntity;
import com.travo.tcms.platform.entity.CurriculumSubjectEntity;
import com.travo.tcms.platform.entity.SchoolProfileEntity;
import com.travo.tcms.platform.entity.StaffDepartmentEntity;
import com.travo.tcms.platform.entity.StudentHouseEntity;
import com.travo.tcms.platform.service.AcademicSetupService;
import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/academics")
public class AcademicSetupController {
  private final AcademicSetupService service;

  public AcademicSetupController(AcademicSetupService service) {
    this.service = service;
  }

  @GetMapping("/options")
  public Map<String, Object> options() {
    return Map.of(
        "schoolTypes", AcademicSetupService.SCHOOL_TYPES,
        "sessions", AcademicSetupService.SESSIONS,
        "schoolStatuses", AcademicSetupService.SCHOOL_STATUSES,
        "recordStatuses", AcademicSetupService.RECORD_STATUSES,
        "subjectTypes", AcademicSetupService.SUBJECT_TYPES,
        "houseColors", AcademicSetupService.HOUSE_COLORS,
        "teachers", AcademicSetupService.TEACHERS
    );
  }

  @GetMapping("/profile")
  public SchoolProfileEntity profile() {
    return service.profile();
  }

  @PutMapping("/profile")
  public SchoolProfileEntity updateProfile(@RequestBody SchoolProfileEntity body) {
    return service.updateProfile(body);
  }

  @GetMapping("/campuses")
  public List<SchoolBranchEntity> campuses() {
    return service.campuses();
  }

  @PutMapping("/campuses/{id}")
  public SchoolBranchEntity updateCampus(@PathVariable Long id, @RequestBody SchoolBranchEntity body) {
    return service.updateCampus(id, body);
  }

  @GetMapping("/classes")
  public List<AcademicClassEntity> classes() {
    return service.classes();
  }

  @PostMapping("/classes")
  public AcademicClassEntity createClass(@RequestBody AcademicClassEntity body) {
    return service.createClass(body);
  }

  @PutMapping("/classes/{id}")
  public AcademicClassEntity updateClass(@PathVariable Long id, @RequestBody AcademicClassEntity body) {
    return service.updateClass(id, body);
  }

  @DeleteMapping("/classes/{id}")
  public ResponseEntity<Void> deleteClass(@PathVariable Long id) {
    service.deleteClass(id);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/sections")
  public List<ClassSectionEntity> sections() {
    return service.sections();
  }

  @PostMapping("/sections")
  public ClassSectionEntity createSection(@RequestBody ClassSectionEntity body) {
    return service.createSection(body);
  }

  @PutMapping("/sections/{id}")
  public ClassSectionEntity updateSection(@PathVariable Long id, @RequestBody ClassSectionEntity body) {
    return service.updateSection(id, body);
  }

  @DeleteMapping("/sections/{id}")
  public ResponseEntity<Void> deleteSection(@PathVariable Long id) {
    service.deleteSection(id);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/subjects")
  public List<CurriculumSubjectEntity> subjects() {
    return service.subjects();
  }

  @PostMapping("/subjects")
  public CurriculumSubjectEntity createSubject(@RequestBody CurriculumSubjectEntity body) {
    return service.createSubject(body);
  }

  @PutMapping("/subjects/{id}")
  public CurriculumSubjectEntity updateSubject(@PathVariable Long id, @RequestBody CurriculumSubjectEntity body) {
    return service.updateSubject(id, body);
  }

  @DeleteMapping("/subjects/{id}")
  public ResponseEntity<Void> deleteSubject(@PathVariable Long id) {
    service.deleteSubject(id);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/departments")
  public List<StaffDepartmentEntity> departments() {
    return service.departments();
  }

  @PostMapping("/departments")
  public StaffDepartmentEntity createDepartment(@RequestBody StaffDepartmentEntity body) {
    return service.createDepartment(body);
  }

  @PutMapping("/departments/{id}")
  public StaffDepartmentEntity updateDepartment(@PathVariable Long id, @RequestBody StaffDepartmentEntity body) {
    return service.updateDepartment(id, body);
  }

  @DeleteMapping("/departments/{id}")
  public ResponseEntity<Void> deleteDepartment(@PathVariable Long id) {
    service.deleteDepartment(id);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/houses")
  public List<StudentHouseEntity> houses() {
    return service.houses();
  }

  @PostMapping("/houses")
  public StudentHouseEntity createHouse(@RequestBody StudentHouseEntity body) {
    return service.createHouse(body);
  }

  @PutMapping("/houses/{id}")
  public StudentHouseEntity updateHouse(@PathVariable Long id, @RequestBody StudentHouseEntity body) {
    return service.updateHouse(id, body);
  }

  @DeleteMapping("/houses/{id}")
  public ResponseEntity<Void> deleteHouse(@PathVariable Long id) {
    service.deleteHouse(id);
    return ResponseEntity.noContent().build();
  }
}

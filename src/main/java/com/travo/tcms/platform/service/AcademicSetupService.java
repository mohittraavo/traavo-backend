package com.travo.tcms.platform.service;

import com.travo.tcms.platform.entity.AcademicClassEntity;
import com.travo.tcms.platform.entity.SchoolBranchEntity;
import com.travo.tcms.platform.entity.ClassSectionEntity;
import com.travo.tcms.platform.entity.CurriculumSubjectEntity;
import com.travo.tcms.platform.entity.SchoolProfileEntity;
import com.travo.tcms.platform.entity.StaffDepartmentEntity;
import com.travo.tcms.platform.entity.StudentHouseEntity;
import com.travo.tcms.platform.repository.AcademicClassRepository;
import com.travo.tcms.platform.repository.SchoolBranchRepository;
import com.travo.tcms.platform.repository.ClassSectionRepository;
import com.travo.tcms.platform.repository.CurriculumSubjectRepository;
import com.travo.tcms.platform.repository.SchoolProfileRepository;
import com.travo.tcms.platform.repository.StaffDepartmentRepository;
import com.travo.tcms.platform.repository.StudentHouseRepository;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AcademicSetupService {
  public static final List<String> SCHOOL_TYPES = List.of(
      "K-12 (Kindergarten to Grade 12)",
      "Primary (Nursery to Grade 5)",
      "Secondary (Grade 6 to Grade 10)",
      "Senior Secondary (Grade 11 to Grade 12)"
  );
  public static final List<String> SESSIONS = List.of(
      "2025 - 2026 (Active Session)",
      "2026 - 2027 (Active Session)",
      "2027 - 2028 (Upcoming Session)"
  );
  public static final List<String> SCHOOL_STATUSES = List.of("Active Operational", "Under Setup", "Inactive");
  public static final List<String> RECORD_STATUSES = List.of("Active", "Inactive");
  public static final List<String> SUBJECT_TYPES = List.of("Core Subject", "Elective", "Optional", "Co-curricular");
  public static final List<String> HOUSE_COLORS = List.of("Red", "Blue", "Green", "Yellow", "Purple", "Orange");
  public static final List<String> TEACHERS = List.of(
      "Dr. Rameshwar Pandey (Social Studies)",
      "Ms. Anjali Mehra (English)",
      "Mr. Vikram Joshi (Mathematics)",
      "Ms. Neha Kapoor (Science)",
      "Mr. Arjun Malhotra (Physics)",
      "Dr. Meera Iyer (Biology)"
  );

  private static final Set<String> SUBJECT_TYPE_SET = Set.copyOf(SUBJECT_TYPES);
  private static final Set<String> COLOR_SET = Set.copyOf(HOUSE_COLORS);

  private final SchoolProfileRepository profiles;
  private final SchoolBranchRepository campuses;
  private final AcademicClassRepository classes;
  private final ClassSectionRepository sections;
  private final CurriculumSubjectRepository subjects;
  private final StaffDepartmentRepository departments;
  private final StudentHouseRepository houses;

  public AcademicSetupService(
      SchoolProfileRepository profiles,
      SchoolBranchRepository campuses,
      AcademicClassRepository classes,
      ClassSectionRepository sections,
      CurriculumSubjectRepository subjects,
      StaffDepartmentRepository departments,
      StudentHouseRepository houses
  ) {
    this.profiles = profiles;
    this.campuses = campuses;
    this.classes = classes;
    this.sections = sections;
    this.subjects = subjects;
    this.departments = departments;
    this.houses = houses;
  }

  public SchoolProfileEntity profile() {
    return profiles.findAll().stream().findFirst()
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "School profile is not set up yet."));
  }

  public SchoolProfileEntity updateProfile(SchoolProfileEntity incoming) {
    SchoolProfileEntity profile = profile();
    String name = required(incoming.schoolName, "School name");
    String code = required(incoming.schoolCode, "School code").toUpperCase();
    String type = oneOf(incoming.schoolType, SCHOOL_TYPES, "School type");
    String contact = required(incoming.contactPerson, "Contact person");
    String email = required(incoming.contactEmail, "Official contact email");
    if (!email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
      throw bad("Enter a valid official contact email.");
    }
    String phone = required(incoming.telephone, "Administrative telephone");
    if (phone.length() < 8) {
      throw bad("Enter a valid administrative telephone.");
    }
    profile.schoolName = name;
    profile.schoolCode = code;
    profile.schoolType = type;
    profile.contactPerson = contact;
    profile.contactEmail = email.toLowerCase();
    profile.telephone = phone;
    profile.website = clean(incoming.website);
    profile.postalAddress = clean(incoming.postalAddress);
    profile.country = clean(incoming.country);
    profile.state = clean(incoming.state);
    profile.city = clean(incoming.city);
    profile.postalCode = clean(incoming.postalCode);
    profile.academicSession = oneOf(incoming.academicSession, SESSIONS, "Active academic session");
    profile.schoolStatus = oneOf(incoming.schoolStatus, SCHOOL_STATUSES, "School status");
    return profiles.save(profile);
  }

  public List<SchoolBranchEntity> campuses() {
    return campuses.findAllByOrderByMainCampusDescNameAsc();
  }

  public SchoolBranchEntity updateCampus(Long id, SchoolBranchEntity incoming) {
    SchoolBranchEntity campus = campuses.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Campus not found."));
    String code = required(incoming.code, "Campus code").toUpperCase();
    boolean clash = campus.id == null
        ? campuses.existsByCodeIgnoreCase(code)
        : campuses.existsByCodeIgnoreCaseAndIdNot(code, campus.id);
    if (clash) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Another campus already uses this code.");
    }
    campus.name = required(incoming.name, "Campus name");
    campus.address = required(incoming.address, "Campus address");
    campus.code = code;
    campus.phone = required(incoming.phone, "Campus phone");
    if (incoming.mainCampus && !campus.mainCampus) {
      for (SchoolBranchEntity other : campuses.findByMainCampusTrue()) {
        if (!other.id.equals(campus.id) && other.mainCampus) {
          other.mainCampus = false;
          campuses.save(other);
        }
      }
      campus.mainCampus = true;
    }
    return campuses.save(campus);
  }

  public List<AcademicClassEntity> classes() {
    return classes.findAllByOrderByDisplayOrderAscNameAsc();
  }

  public AcademicClassEntity createClass(AcademicClassEntity incoming) {
    AcademicClassEntity item = new AcademicClassEntity();
    applyClass(item, incoming);
    return classes.save(item);
  }

  public AcademicClassEntity updateClass(Long id, AcademicClassEntity incoming) {
    AcademicClassEntity item = classes.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Class not found."));
    applyClass(item, incoming);
    return classes.save(item);
  }

  public void deleteClass(Long id) {
    if (!classes.existsById(id)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Class not found.");
    }
    if (sections.existsByClassId(id)) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Remove this class's sections before deleting it.");
    }
    classes.deleteById(id);
  }

  public List<ClassSectionEntity> sections() {
    var order = classes().stream().collect(java.util.stream.Collectors.toMap(item -> item.id, item -> item.displayOrder == null ? 0 : item.displayOrder));
    return sections.findAll().stream()
        .sorted(Comparator.comparingInt((ClassSectionEntity item) -> order.getOrDefault(item.classId, 999)).thenComparing(item -> item.name == null ? "" : item.name))
        .toList();
  }

  public ClassSectionEntity createSection(ClassSectionEntity incoming) {
    ClassSectionEntity item = new ClassSectionEntity();
    applySection(item, incoming);
    return sections.save(item);
  }

  public ClassSectionEntity updateSection(Long id, ClassSectionEntity incoming) {
    ClassSectionEntity item = sections.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Section not found."));
    applySection(item, incoming);
    return sections.save(item);
  }

  public void deleteSection(Long id) {
    if (!sections.existsById(id)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Section not found.");
    }
    sections.deleteById(id);
  }

  public List<CurriculumSubjectEntity> subjects() {
    return subjects.findAllByOrderByDisplayOrderAscNameAsc();
  }

  public CurriculumSubjectEntity createSubject(CurriculumSubjectEntity incoming) {
    CurriculumSubjectEntity item = new CurriculumSubjectEntity();
    applySubject(item, incoming);
    return subjects.save(item);
  }

  public CurriculumSubjectEntity updateSubject(Long id, CurriculumSubjectEntity incoming) {
    CurriculumSubjectEntity item = subjects.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Subject not found."));
    applySubject(item, incoming);
    return subjects.save(item);
  }

  public void deleteSubject(Long id) {
    if (!subjects.existsById(id)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Subject not found.");
    }
    subjects.deleteById(id);
  }

  public List<StaffDepartmentEntity> departments() {
    return departments.findAllByOrderByIdAsc();
  }

  public StaffDepartmentEntity createDepartment(StaffDepartmentEntity incoming) {
    StaffDepartmentEntity item = new StaffDepartmentEntity();
    applyDepartment(item, incoming);
    return departments.save(item);
  }

  public StaffDepartmentEntity updateDepartment(Long id, StaffDepartmentEntity incoming) {
    StaffDepartmentEntity item = departments.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Department not found."));
    applyDepartment(item, incoming);
    return departments.save(item);
  }

  public void deleteDepartment(Long id) {
    if (!departments.existsById(id)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Department not found.");
    }
    departments.deleteById(id);
  }

  public List<StudentHouseEntity> houses() {
    return houses.findAllByOrderByNameAsc();
  }

  public StudentHouseEntity createHouse(StudentHouseEntity incoming) {
    StudentHouseEntity item = new StudentHouseEntity();
    applyHouse(item, incoming);
    return houses.save(item);
  }

  public StudentHouseEntity updateHouse(Long id, StudentHouseEntity incoming) {
    StudentHouseEntity item = houses.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "House not found."));
    applyHouse(item, incoming);
    return houses.save(item);
  }

  public void deleteHouse(Long id) {
    if (!houses.existsById(id)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "House not found.");
    }
    houses.deleteById(id);
  }

  private void applyClass(AcademicClassEntity item, AcademicClassEntity incoming) {
    SchoolBranchEntity campus = requireCampus(incoming.campusId);
    String code = required(incoming.code, "Class code").toUpperCase();
    ensureCode(code, item.id, classes.existsByCodeIgnoreCase(code), item.id != null && classes.existsByCodeIgnoreCaseAndIdNot(code, item.id), "class");
    item.academicYear = year(incoming.academicYear);
    item.campusId = campus.id;
    item.name = required(incoming.name, "Class name");
    item.code = code;
    item.displayOrder = order(incoming.displayOrder, classes.count() + 1);
    item.status = status(incoming.status);
  }

  private void applySection(ClassSectionEntity item, ClassSectionEntity incoming) {
    SchoolBranchEntity campus = requireCampus(incoming.campusId);
    AcademicClassEntity parent = classes.findById(incoming.classId == null ? -1L : incoming.classId)
        .orElseThrow(() -> bad("Choose a parent class."));
    String code = required(incoming.code, "Section code").toUpperCase();
    ensureCode(code, item.id, sections.existsByCodeIgnoreCase(code), item.id != null && sections.existsByCodeIgnoreCaseAndIdNot(code, item.id), "section");
    String teacher = clean(incoming.teacher);
    if (!teacher.isEmpty() && !TEACHERS.contains(teacher)) {
      throw bad("Choose a class teacher from the staff list.");
    }
    item.academicYear = year(incoming.academicYear);
    item.campusId = campus.id;
    item.classId = parent.id;
    item.name = required(incoming.name, "Section name");
    item.code = code;
    int capacity = incoming.capacity == null ? 0 : incoming.capacity;
    if (capacity < 1 || capacity > 200) {
      throw bad("Student capacity must be between 1 and 200.");
    }
    item.capacity = capacity;
    item.status = status(incoming.status);
    item.teacher = teacher;
  }

  private void applySubject(CurriculumSubjectEntity item, CurriculumSubjectEntity incoming) {
    String code = required(incoming.code, "Subject code").toUpperCase();
    ensureCode(code, item.id, subjects.existsByCodeIgnoreCase(code), item.id != null && subjects.existsByCodeIgnoreCaseAndIdNot(code, item.id), "subject");
    String type = clean(incoming.subjectType);
    if (type.isEmpty()) type = "Core Subject";
    if (!SUBJECT_TYPE_SET.contains(type)) {
      throw bad("Choose a valid subject type.");
    }
    item.academicYear = year(incoming.academicYear);
    item.name = required(incoming.name, "Subject name");
    item.code = code;
    item.subjectType = type;
    item.displayOrder = order(incoming.displayOrder, subjects.count() + 1);
    item.status = status(incoming.status);
  }

  private void applyDepartment(StaffDepartmentEntity item, StaffDepartmentEntity incoming) {
    String name = required(incoming.name, "Department name");
    String code = clean(incoming.code).toUpperCase();
    if (code.isEmpty()) {
      code = "DEP-" + name.replaceAll("[^A-Za-z]", "").toUpperCase();
      if (code.length() > 12) code = code.substring(0, 12);
    }
    ensureCode(code, item.id, departments.existsByCodeIgnoreCase(code), item.id != null && departments.existsByCodeIgnoreCaseAndIdNot(code, item.id), "department");
    item.name = name;
    item.code = code;
  }

  private void applyHouse(StudentHouseEntity item, StudentHouseEntity incoming) {
    String code = required(incoming.code, "House code").toUpperCase();
    ensureCode(code, item.id, houses.existsByCodeIgnoreCase(code), item.id != null && houses.existsByCodeIgnoreCaseAndIdNot(code, item.id), "house");
    String color = clean(incoming.color);
    if (color.isEmpty()) color = "Blue";
    if (!COLOR_SET.contains(color)) {
      throw bad("Choose a house color.");
    }
    item.name = required(incoming.name, "House name");
    item.code = code;
    item.color = color;
    item.description = clean(incoming.description);
  }

  private SchoolBranchEntity requireCampus(Long id) {
    if (id == null) throw bad("Choose a campus.");
    return campuses.findById(id).orElseThrow(() -> bad("Choose a campus."));
  }

  private void ensureCode(String code, Long id, boolean exists, boolean existsOther, String label) {
    boolean clash = id == null ? exists : existsOther;
    if (clash) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Another " + label + " already uses this code.");
    }
  }

  private String year(String value) {
    String cleaned = clean(value).replace(" ", "");
    if (cleaned.isEmpty()) return "2026-2027";
    if (!cleaned.matches("\\d{4}-\\d{4}")) {
      throw bad("Academic year must look like 2026-2027.");
    }
    return cleaned;
  }

  private String status(String value) {
    String cleaned = clean(value);
    if (cleaned.isEmpty()) return "Active";
    if (!RECORD_STATUSES.contains(cleaned)) throw bad("Status must be Active or Inactive.");
    return cleaned;
  }

  private int order(Integer value, long fallback) {
    int order = value == null || value < 1 ? (int) fallback : value;
    if (order > 999) throw bad("Display order must be between 1 and 999.");
    return order;
  }

  private String oneOf(String value, List<String> allowed, String label) {
    String cleaned = required(value, label);
    if (!allowed.contains(cleaned)) throw bad("Choose a valid " + label.toLowerCase() + ".");
    return cleaned;
  }

  private String required(String value, String label) {
    String cleaned = clean(value);
    if (cleaned.isEmpty()) throw bad(label + " is required.");
    if (cleaned.length() > 160) throw bad(label + " is too long.");
    return cleaned;
  }

  private String clean(String value) {
    return value == null ? "" : value.trim().replaceAll("\\s+", " ");
  }

  private ResponseStatusException bad(String message) {
    return new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
  }
}

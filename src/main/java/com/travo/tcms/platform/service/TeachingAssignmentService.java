package com.travo.tcms.platform.service;

import com.travo.tcms.platform.dto.TeachingRosterRow;
import com.travo.tcms.platform.entity.AcademicClassEntity;
import com.travo.tcms.platform.entity.ClassSectionEntity;
import com.travo.tcms.platform.entity.StaffMemberEntity;
import com.travo.tcms.platform.entity.TeachingAssignmentEntity;
import com.travo.tcms.platform.repository.AcademicClassRepository;
import com.travo.tcms.platform.repository.ClassSectionRepository;
import com.travo.tcms.platform.repository.StaffMemberRepository;
import com.travo.tcms.platform.repository.TeachingAssignmentRepository;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TeachingAssignmentService {
  public static final List<String> DUTY_TYPES = List.of("Core", "Elective", "Co-Curricular", "Support");
  private static final int DEFAULT_CAPACITY = 26;

  private final TeachingAssignmentRepository assignments;
  private final StaffMemberRepository staff;
  private final AcademicClassRepository classes;
  private final ClassSectionRepository sections;

  public TeachingAssignmentService(
      TeachingAssignmentRepository assignments,
      StaffMemberRepository staff,
      AcademicClassRepository classes,
      ClassSectionRepository sections
  ) {
    this.assignments = assignments;
    this.staff = staff;
    this.classes = classes;
    this.sections = sections;
  }

  public List<TeachingAssignmentEntity> list() {
    return assignments.findAll();
  }

  public List<TeachingRosterRow> roster() {
    Map<Long, List<TeachingAssignmentEntity>> byStaff = new HashMap<>();
    for (TeachingAssignmentEntity item : assignments.findAll()) {
      if (item.staffId == null) {
        continue;
      }
      byStaff.computeIfAbsent(item.staffId, key -> new ArrayList<>()).add(item);
    }
    List<TeachingRosterRow> rows = new ArrayList<>();
    for (StaffMemberEntity person : staff.findAll()) {
      if (person.workforce != null && !"Teaching".equalsIgnoreCase(person.workforce) && !"Faculty".equalsIgnoreCase(person.category)) {
        continue;
      }
      List<TeachingAssignmentEntity> mine = byStaff.getOrDefault(person.id, List.of());
      TeachingRosterRow row = new TeachingRosterRow();
      row.staffId = person.id;
      row.name = displayName(person);
      row.employeeCode = person.employeeCode == null ? "" : person.employeeCode;
      row.department = person.department == null ? "" : person.department;
      row.designation = person.designation == null ? "" : person.designation;
      row.campus = person.campus == null ? "" : person.campus;
      row.weeklyCapacity = mine.stream().map(item -> item.weeklyCapacity == null ? 0 : item.weeklyCapacity).max(Integer::compareTo).orElse(DEFAULT_CAPACITY);
      if (row.weeklyCapacity < 1) row.weeklyCapacity = DEFAULT_CAPACITY;
      row.assignedPeriods = mine.stream().mapToInt(item -> item.weeklyPeriods == null ? 0 : item.weeklyPeriods).sum();
      row.capacityLeft = row.weeklyCapacity - row.assignedPeriods;
      row.classTeacher = mine.stream().anyMatch(item -> Boolean.TRUE.equals(item.classTeacher));
      row.classesLabel = join(mine, item -> shortClass(item.className));
      row.sectionsLabel = join(mine, item -> shortClass(item.className) + "-" + shortSection(item.sectionName));
      row.subjectsLabel = join(mine, item -> item.subject == null ? "" : item.subject);
      row.conflict = row.assignedPeriods > row.weeklyCapacity ? "Over capacity" : "Clean";
      row.assignments = mine;
      rows.add(row);
    }
    rows.sort(Comparator.comparing(item -> item.name));
    return rows;
  }

  @Transactional
  public TeachingAssignmentEntity create(TeachingAssignmentEntity incoming) {
    TeachingAssignmentEntity item = new TeachingAssignmentEntity();
    apply(item, incoming, null);
    TeachingAssignmentEntity saved = assignments.save(item);
    syncClassTeacher(saved);
    return saved;
  }

  @Transactional
  public TeachingAssignmentEntity update(Long id, TeachingAssignmentEntity incoming) {
    TeachingAssignmentEntity item = assignments.findById(id).orElseThrow(() -> new IllegalArgumentException("Assignment not found."));
    apply(item, incoming, id);
    TeachingAssignmentEntity saved = assignments.save(item);
    syncClassTeacher(saved);
    return saved;
  }

  @Transactional
  public void delete(Long id) {
    if (!assignments.existsById(id)) throw new IllegalArgumentException("Assignment not found.");
    assignments.deleteById(id);
  }

  private void apply(TeachingAssignmentEntity item, TeachingAssignmentEntity incoming, Long currentId) {
    StaffMemberEntity person = staff.findById(incoming.staffId == null ? -1L : incoming.staffId)
        .orElseThrow(() -> new IllegalArgumentException("Choose a teacher."));
    String duty = clean(incoming.dutyType);
    if (!DUTY_TYPES.contains(duty)) throw new IllegalArgumentException("Choose a duty type.");
    String className = required(incoming.className, "Class");
    String sectionName = required(incoming.sectionName, "Section");
    String subject = required(incoming.subject, "Subject");
    int theory = nonNegative(incoming.theoryPeriods, "Theory load");
    int practical = nonNegative(incoming.practicalPeriods, "Practical / lab");
    int weekly = incoming.weeklyPeriods == null ? theory + practical : nonNegative(incoming.weeklyPeriods, "Weekly periods");
    if (weekly < 1) throw new IllegalArgumentException("Weekly periods must be at least 1.");
    if (theory + practical > 0 && theory + practical != weekly) {
      throw new IllegalArgumentException("Theory and practical periods must add up to the weekly periods.");
    }
    int capacity = incoming.weeklyCapacity == null || incoming.weeklyCapacity < 1 ? DEFAULT_CAPACITY : incoming.weeklyCapacity;
    boolean classTeacher = Boolean.TRUE.equals(incoming.classTeacher);
    if (classTeacher) {
      List<TeachingAssignmentEntity> holders = assignments
          .findByAcademicYearAndClassNameIgnoreCaseAndSectionNameIgnoreCaseAndClassTeacherTrue(year(incoming.academicYear), className, sectionName);
      boolean taken = holders.stream().anyMatch(other -> currentId == null || !currentId.equals(other.id));
      if (taken) {
        throw new IllegalArgumentException(className + " " + sectionName + " already has a class teacher.");
      }
    }
    item.staffId = person.id;
    item.academicYear = year(incoming.academicYear);
    item.campus = clean(incoming.campus).isEmpty() ? (person.campus == null ? "Main Campus" : person.campus) : clean(incoming.campus);
    item.dutyType = duty;
    item.className = className;
    item.sectionName = sectionName;
    item.subject = subject;
    item.weeklyPeriods = weekly;
    item.theoryPeriods = theory;
    item.practicalPeriods = practical;
    item.classTeacher = classTeacher;
    item.effectiveFrom = clean(incoming.effectiveFrom);
    item.effectiveTo = clean(incoming.effectiveTo);
    item.notes = clean(incoming.notes);
    item.weeklyCapacity = capacity;
    item.status = clean(incoming.status).isEmpty() ? "Active" : clean(incoming.status);
  }

  private void syncClassTeacher(TeachingAssignmentEntity item) {
    if (!Boolean.TRUE.equals(item.classTeacher)) return;
    StaffMemberEntity person = staff.findById(item.staffId).orElse(null);
    if (person == null) return;
    AcademicClassEntity parent = classes.findFirstByNameIgnoreCase(item.className).orElse(null);
    if (parent == null) {
      return;
    }
    for (ClassSectionEntity section : sections.findByClassIdAndNameIgnoreCase(parent.id, item.sectionName)) {
      section.teacher = displayName(person) + " (" + item.subject + ")";
      sections.save(section);
    }
  }

  private static String displayName(StaffMemberEntity person) {
    String salutation = person.salutation == null ? "" : person.salutation.trim();
    String name = ((person.firstName == null ? "" : person.firstName) + " " + (person.lastName == null ? "" : person.lastName)).trim();
    return (salutation.isEmpty() ? "" : salutation + " ") + name;
  }

  private static String join(List<TeachingAssignmentEntity> items, java.util.function.Function<TeachingAssignmentEntity, String> mapper) {
    Set<String> values = new LinkedHashSet<>();
    for (TeachingAssignmentEntity item : items) {
      String value = mapper.apply(item);
      if (value != null && !value.isBlank()) values.add(value);
    }
    return values.isEmpty() ? "—" : String.join(", ", values);
  }

  private static String shortClass(String name) {
    if (name == null) return "";
    return name.replace("Grade ", "").replace("Class ", "").trim();
  }

  private static String shortSection(String name) {
    if (name == null) return "";
    return name.replace("Section ", "").trim();
  }

  private static String year(String value) {
    String clean = clean(value);
    return clean.isEmpty() ? "2026-2027" : clean;
  }

  private static String required(String value, String label) {
    String clean = clean(value);
    if (clean.isEmpty()) throw new IllegalArgumentException(label + " is required.");
    return clean;
  }

  private static int nonNegative(Integer value, String label) {
    int number = value == null ? 0 : value;
    if (number < 0) throw new IllegalArgumentException(label + " cannot be negative.");
    return number;
  }

  private static String clean(String value) {
    return value == null ? "" : value.trim();
  }
}

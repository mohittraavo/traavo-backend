package com.travo.tcms.platform.service;

import com.travo.tcms.platform.entity.FeeStructureEntity;
import com.travo.tcms.platform.model.ClassFeeSchedule;
import com.travo.tcms.platform.model.FeeLine;
import com.travo.tcms.platform.repository.FeeStructureRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
public class FeeStructureService {
  static final List<String> CLASSES = List.of(
      "Nursery", "LKG", "UKG",
      "Class 1", "Class 2", "Class 3", "Class 4", "Class 5",
      "Class 6", "Class 7", "Class 8",
      "Class 9", "Class 10", "Class 11", "Class 12"
  );
  static final List<String> CAMPUSES = List.of(
      "Main Campus (Vasant Kunj)", "East Campus", "South Campus"
  );
  static final Set<String> FREQUENCIES = Set.of(
      "Monthly", "Quarterly", "Half Yearly", "Annual (1x Full Year)", "Term", "One Time"
  );
  static final Set<String> STRUCTURE_STATUSES = Set.of("Draft", "Active", "Inactive");
  static final Set<String> CLASS_STATUSES = Set.of("Draft", "Active");
  static final Set<String> COMPONENT_TYPES = Set.of("Required", "Optional");

  private final FeeStructureRepository repo;

  public FeeStructureService(FeeStructureRepository repo) {
    this.repo = repo;
  }

  @Transactional(readOnly = true)
  public List<FeeStructureEntity> list(java.util.Map<String, String> filters) {
    return repo.findAll().stream()
        .map(this::hydrate)
        .filter(item -> matches(item, filters))
        .sorted(Comparator.comparing((FeeStructureEntity item) -> item.updatedDate == null ? "" : item.updatedDate).reversed())
        .toList();
  }

  @Transactional(readOnly = true)
  public FeeStructureEntity get(String id) {
    return repo.findById(id).map(this::hydrate)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Fee structure not found"));
  }

  @Transactional
  public FeeStructureEntity create(FeeStructureEntity incoming) {
    FeeStructureEntity structure = normalize(incoming, true);
    structure.id = UUID.randomUUID().toString();
    structure.createdDate = LocalDate.now().toString();
    structure.updatedDate = structure.createdDate;
    ensureUnique(structure.id, structure.name, structure.academicYear, structure.campus);
    return repo.save(structure);
  }

  @Transactional
  public FeeStructureEntity update(String id, FeeStructureEntity incoming) {
    FeeStructureEntity existing = repo.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Fee structure not found"));
    FeeStructureEntity structure = normalize(incoming, false);
    structure.id = existing.id;
    structure.createdDate = existing.createdDate == null || existing.createdDate.isBlank()
        ? LocalDate.now().toString() : existing.createdDate;
    structure.updatedDate = LocalDate.now().toString();
    ensureUnique(structure.id, structure.name, structure.academicYear, structure.campus);
    return repo.save(structure);
  }

  @Transactional
  public void delete(String id) {
    if (!repo.existsById(id)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Fee structure not found");
    }
    repo.deleteById(id);
  }

  private FeeStructureEntity hydrate(FeeStructureEntity item) {
    if (item.templateComponents == null) item.templateComponents = new ArrayList<>();
    if (item.classSchedules == null) item.classSchedules = new ArrayList<>();
    if (item.defaultConcession == null) item.defaultConcession = money(BigDecimal.ZERO);
    if (item.defaultLateFine == null) item.defaultLateFine = money(BigDecimal.ZERO);
    if (item.status == null || item.status.isBlank()) item.status = "Draft";
    return item;
  }

  private FeeStructureEntity normalize(FeeStructureEntity incoming, boolean creating) {
    if (incoming == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Fee structure body is required");
    }
    List<String> errors = new ArrayList<>();
    String name = clean(incoming.name);
    String academicYear = clean(incoming.academicYear);
    String campus = clean(incoming.campus);
    String status = clean(incoming.status);
    if (status.isEmpty()) status = "Draft";

    if (name.length() < 3 || name.length() > 120) {
      errors.add("Fee structure name must be between 3 and 120 characters.");
    }
    if (!academicYear.matches("\\d{4}-\\d{4}") || !consecutiveYear(academicYear)) {
      errors.add("Academic year must look like 2026-2027.");
    }
    if (!CAMPUSES.contains(campus)) {
      errors.add("Campus is not recognized.");
    }
    if (!STRUCTURE_STATUSES.contains(status)) {
      errors.add("Status must be Draft, Active, or Inactive.");
    }

    String effectiveFrom = clean(incoming.effectiveFrom);
    String effectiveTo = clean(incoming.effectiveTo);
    LocalDate from = parseDate(effectiveFrom, "Effective from", errors);
    LocalDate to = parseDate(effectiveTo, "Effective to", errors);
    if (from != null && to != null && to.isBefore(from)) {
      errors.add("Effective to cannot be before effective from.");
    }

    boolean strict = !"Draft".equals(status);
    BigDecimal defaultConcession = moneyOrError(incoming.defaultConcession, "Default concession", errors);
    BigDecimal defaultLateFine = moneyOrError(incoming.defaultLateFine, "Default late fine", errors);

    List<FeeLine> template = normalizeLines(incoming.templateComponents, "Template", strict, errors);
    if (strict && template.isEmpty()) {
      errors.add("Add at least one fee component before saving the configuration.");
    }

    List<ClassFeeSchedule> schedules = normalizeSchedules(incoming.classSchedules, strict, errors);
    if (strict && schedules.isEmpty()) {
      errors.add("Select at least one class.");
    }

    if (!errors.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.join(" ", errors));
    }

    FeeStructureEntity structure = new FeeStructureEntity();
    structure.name = name;
    structure.academicYear = academicYear;
    structure.campus = campus;
    structure.effectiveFrom = effectiveFrom;
    structure.effectiveTo = effectiveTo;
    structure.status = status;
    structure.defaultConcession = defaultConcession;
    structure.defaultLateFine = defaultLateFine;
    structure.templateComponents = template;
    structure.classSchedules = schedules;
    if (!creating) {
      structure.id = incoming.id;
    }
    return structure;
  }

  private List<FeeLine> normalizeLines(List<FeeLine> incoming, String scope, boolean strict, List<String> errors) {
    List<FeeLine> lines = new ArrayList<>();
    if (incoming == null || incoming.isEmpty()) {
      return lines;
    }
    if (incoming.size() > 40) {
      errors.add(scope + " cannot contain more than 40 components.");
      return lines;
    }
    Set<String> heads = new HashSet<>();
    for (int index = 0; index < incoming.size(); index += 1) {
      FeeLine source = incoming.get(index);
      if (source == null) {
        errors.add(scope + " component " + (index + 1) + " is empty.");
        continue;
      }
      FeeLine line = new FeeLine();
      line.id = clean(source.id);
      if (line.id.isEmpty()) line.id = UUID.randomUUID().toString();
      line.feeHead = clean(source.feeHead);
      line.frequency = clean(source.frequency);
      line.dueDate = clean(source.dueDate);
      line.type = clean(source.type);
      if (line.type.isEmpty()) line.type = "Required";
      line.discountEligible = source.discountEligible;
      line.lateFine = source.lateFine;
      line.amount = moneyOrError(source.amount, scope + " amount for " + (line.feeHead.isEmpty() ? "component " + (index + 1) : line.feeHead), errors);

      if (line.feeHead.length() < 2 || line.feeHead.length() > 80) {
        errors.add(scope + " component " + (index + 1) + " needs a fee head.");
      } else if (!heads.add(line.feeHead.toLowerCase(Locale.ROOT))) {
        errors.add(scope + " has a duplicate fee head: " + line.feeHead + ".");
      }
      if (line.frequency.isEmpty()) {
        if (strict) errors.add(scope + " frequency is required for " + displayHead(line, index) + ".");
      } else if (!FREQUENCIES.contains(line.frequency)) {
        errors.add(scope + " frequency is invalid for " + displayHead(line, index) + ".");
      }
      if (!COMPONENT_TYPES.contains(line.type)) {
        errors.add(scope + " type must be Required or Optional.");
      }
      if (strict && line.amount.signum() <= 0) {
        errors.add(scope + " amount must be greater than 0 for " + displayHead(line, index) + ".");
      }
      if (strict && line.dueDate.isEmpty()) {
        errors.add(scope + " due date is required for " + displayHead(line, index) + ".");
      }
      if (!line.dueDate.isEmpty()) {
        parseDate(line.dueDate, scope + " due date for " + displayHead(line, index), errors);
      }
      lines.add(line);
    }
    return lines;
  }

  private List<ClassFeeSchedule> normalizeSchedules(List<ClassFeeSchedule> incoming, boolean strict, List<String> errors) {
    List<ClassFeeSchedule> schedules = new ArrayList<>();
    if (incoming == null || incoming.isEmpty()) {
      return schedules;
    }
    Set<String> names = new HashSet<>();
    for (ClassFeeSchedule source : incoming) {
      if (source == null) continue;
      ClassFeeSchedule schedule = new ClassFeeSchedule();
      schedule.className = clean(source.className);
      schedule.sectionsScope = clean(source.sectionsScope);
      if (schedule.sectionsScope.isEmpty()) schedule.sectionsScope = "Entire Class (All)";
      if (schedule.sectionsScope.length() > 80) {
        errors.add("Section scope is too long for " + schedule.className + ".");
      }
      schedule.status = clean(source.status);
      if (schedule.status.isEmpty()) schedule.status = "Draft";
      schedule.concession = moneyOrError(source.concession, "Concession for " + displayClass(schedule.className), errors);
      schedule.lateFineAmount = moneyOrError(source.lateFineAmount, "Late fine for " + displayClass(schedule.className), errors);

      if (!CLASSES.contains(schedule.className)) {
        errors.add("Class " + displayClass(schedule.className) + " is not recognized.");
      } else if (!names.add(schedule.className)) {
        errors.add(schedule.className + " is selected more than once.");
      }
      if (!CLASS_STATUSES.contains(schedule.status)) {
        errors.add("Class status must be Draft or Active.");
      }

      schedule.components = normalizeLines(source.components, schedule.className.isEmpty() ? "Class" : schedule.className, strict, errors);
      if (strict && schedule.components.isEmpty()) {
        errors.add(displayClass(schedule.className) + " needs at least one fee component.");
      }
      BigDecimal base = schedule.components.stream()
          .map(line -> line.amount == null ? BigDecimal.ZERO : line.amount)
          .reduce(BigDecimal.ZERO, BigDecimal::add);
      if (schedule.concession.compareTo(base) > 0) {
        errors.add("Concession cannot exceed the base fee for " + displayClass(schedule.className) + ".");
      }
      schedules.add(schedule);
    }
    schedules.sort(Comparator.comparingInt(item -> {
      int index = CLASSES.indexOf(item.className);
      return index < 0 ? Integer.MAX_VALUE : index;
    }));
    return schedules;
  }

  private void ensureUnique(String id, String name, String academicYear, String campus) {
    boolean clash = repo.existsDuplicate(name, academicYear, campus, id == null ? "" : id);
    if (clash) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "A fee structure with this name already exists for the academic year and campus.");
    }
  }

  private boolean matches(FeeStructureEntity item, java.util.Map<String, String> filters) {
    if (filters == null || filters.isEmpty()) return true;
    for (var entry : filters.entrySet()) {
      String value = entry.getValue();
      if (value == null || value.isBlank()) continue;
      String needle = value.trim().toLowerCase(Locale.ROOT);
      boolean ok = switch (entry.getKey()) {
        case "q" -> contains(item.name, needle)
            || contains(item.academicYear, needle)
            || contains(item.campus, needle)
            || item.classSchedules.stream().anyMatch(schedule -> contains(schedule.className, needle));
        case "academicYear" -> equals(item.academicYear, needle);
        case "campus" -> equals(item.campus, needle);
        case "status" -> equals(item.status, needle);
        case "className" -> item.classSchedules.stream().anyMatch(schedule -> equals(schedule.className, needle));
        default -> true;
      };
      if (!ok) return false;
    }
    return true;
  }

  private static String displayHead(FeeLine line, int index) {
    return line.feeHead == null || line.feeHead.isBlank() ? "component " + (index + 1) : line.feeHead;
  }

  private static String displayClass(String className) {
    return className == null || className.isBlank() ? "a class" : className;
  }

  private static boolean consecutiveYear(String academicYear) {
    int start = Integer.parseInt(academicYear.substring(0, 4));
    int end = Integer.parseInt(academicYear.substring(5));
    return end == start + 1;
  }

  private static LocalDate parseDate(String value, String label, List<String> errors) {
    if (value == null || value.isBlank()) return null;
    try {
      return LocalDate.parse(value);
    } catch (DateTimeParseException exception) {
      errors.add(label + " must be a real date in YYYY-MM-DD format.");
      return null;
    }
  }

  private static BigDecimal moneyOrError(BigDecimal value, String label, List<String> errors) {
    if (value == null) return money(BigDecimal.ZERO);
    if (value.signum() < 0) {
      errors.add(label + " cannot be negative.");
      return money(BigDecimal.ZERO);
    }
    if (value.compareTo(new BigDecimal("100000000")) > 0) {
      errors.add(label + " is too large.");
      return money(BigDecimal.ZERO);
    }
    return money(value);
  }

  private static BigDecimal money(BigDecimal value) {
    return value.setScale(2, RoundingMode.HALF_UP);
  }

  private static String clean(String value) {
    return value == null ? "" : value.trim();
  }

  private static boolean contains(String source, String needle) {
    return source != null && source.toLowerCase(Locale.ROOT).contains(needle);
  }

  private static boolean equals(String source, String needle) {
    return source != null && source.trim().equalsIgnoreCase(needle);
  }
}

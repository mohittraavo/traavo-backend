package com.travo.tcms.platform.service;

import com.travo.tcms.platform.entity.StaffMemberEntity;
import com.travo.tcms.platform.repository.StaffMemberRepository;
import com.travo.tcms.platform.util.StaffCsv;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class StaffMemberService {
  public static final List<String> WORKFORCES = List.of("Teaching", "Support");
  public static final List<String> STATUSES = List.of("Active", "Probation", "Notice", "Inactive");
  public static final List<String> EMPLOYMENT_TYPES = List.of("Full Time", "Part Time", "Contract");
  public static final List<String> DEPARTMENTS = List.of(
      "Mathematics", "Science", "Languages", "Social Studies", "Administration", "Operations",
      "Accounts & Finance", "Library Services", "Transport"
  );
  public static final List<String> DESIGNATIONS = List.of(
      "Faculty", "Assistant Teacher", "Senior Faculty", "Senior Faculty & Head of Dept",
      "Finance Controller", "Admissions Officer", "Senior Accountant", "Chief Librarian", "Transport Supervisor"
  );

  private final StaffMemberRepository repository;

  public StaffMemberService(StaffMemberRepository repository) {
    this.repository = repository;
  }

  public List<StaffMemberEntity> list() {
    return repository.findAll().stream()
        .sorted(Comparator.comparing((StaffMemberEntity item) -> item.firstName == null ? "" : item.firstName, String.CASE_INSENSITIVE_ORDER))
        .toList();
  }

  public StaffMemberEntity create(StaffMemberEntity incoming) {
    StaffMemberEntity staff = new StaffMemberEntity();
    apply(staff, incoming, null);
    return repository.save(staff);
  }

  public StaffMemberEntity update(Long id, StaffMemberEntity incoming) {
    StaffMemberEntity staff = repository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Staff member not found."));
    apply(staff, incoming, id);
    return repository.save(staff);
  }

  public void delete(Long id) {
    if (!repository.existsById(id)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Staff member not found.");
    }
    repository.deleteById(id);
  }

  public Map<String, Object> importRows(String csv) {
    List<Map<String, String>> rows = StaffCsv.parse(csv);
    if (rows.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The file has no staff rows.");
    }
    Set<String> seen = new HashSet<>();
    Set<String> existingCodes = repository.findEmployeeCodes().stream()
        .map(code -> code.trim().toLowerCase(Locale.ROOT))
        .collect(java.util.stream.Collectors.toSet());
    List<StaffMemberEntity> valid = new ArrayList<>();
    List<Map<String, Object>> errors = new ArrayList<>();
    for (Map<String, String> row : rows) {
      int line = Integer.parseInt(row.getOrDefault("_row", "0"));
      StaffMemberEntity incoming = StaffCsv.toEntity(row);
      List<String> messages = new ArrayList<>();
      if (incoming.employeeCode.isBlank()) messages.add("Employee code is required.");
      if (incoming.firstName.isBlank()) messages.add("First name is required.");
      String code = incoming.employeeCode.toLowerCase(Locale.ROOT);
      if (!code.isBlank() && !seen.add(code)) messages.add("Duplicate employee code in this file.");
      if (!code.isBlank() && existingCodes.contains(code)) {
        messages.add("Employee code already exists.");
      }
      if (!incoming.email.isBlank() && !incoming.email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
        messages.add("Email is not valid.");
      }
      if (!incoming.joiningDate.isBlank() && !incoming.joiningDate.matches("\\d{4}-\\d{2}-\\d{2}")) {
        messages.add("Joining date must be YYYY-MM-DD.");
      }
      if (!messages.isEmpty()) {
        Map<String, Object> error = new HashMap<>();
        error.put("row", line);
        error.put("employeeCode", incoming.employeeCode);
        error.put("messages", messages);
        errors.add(error);
        continue;
      }
      StaffMemberEntity staff = new StaffMemberEntity();
      apply(staff, incoming, null);
      valid.add(staff);
    }
    repository.saveAll(valid);
    Map<String, Object> body = new HashMap<>();
    body.put("imported", valid.size());
    body.put("failed", errors.size());
    body.put("total", rows.size());
    body.put("errors", errors);
    return body;
  }

  public String exportCsv() {
    StringBuilder csv = new StringBuilder("EmployeeCode,FirstName,LastName,Department,Designation,Category,Phone,Email,Campus,JoiningDate,EmploymentType\n");
    for (StaffMemberEntity staff : list()) {
      csv.append(cell(staff.employeeCode)).append(',')
          .append(cell(staff.firstName)).append(',')
          .append(cell(staff.lastName)).append(',')
          .append(cell(staff.department)).append(',')
          .append(cell(staff.designation)).append(',')
          .append(cell(staff.category)).append(',')
          .append(cell(staff.phone)).append(',')
          .append(cell(staff.email)).append(',')
          .append(cell(staff.campus)).append(',')
          .append(cell(staff.joiningDate)).append(',')
          .append(cell(staff.employmentType)).append('\n');
    }
    return csv.toString();
  }

  private void apply(StaffMemberEntity staff, StaffMemberEntity incoming, Long id) {
    String code = required(incoming.employeeCode, "Employee code").toUpperCase(Locale.ROOT);
    boolean clash = id == null
        ? repository.existsByEmployeeCodeIgnoreCase(code)
        : repository.existsByEmployeeCodeIgnoreCaseAndIdNot(code, id);
    if (clash) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Another staff member already uses this employee code.");
    }
    String email = clean(incoming.email);
    if (!email.isEmpty() && !email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Enter a valid official email.");
    }
    String joining = clean(incoming.joiningDate);
    if (!joining.isEmpty() && !joining.matches("\\d{4}-\\d{2}-\\d{2}")) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Joining date must be YYYY-MM-DD.");
    }
    staff.employeeCode = code;
    staff.salutation = clean(incoming.salutation);
    staff.firstName = required(incoming.firstName, "First name");
    staff.middleName = clean(incoming.middleName);
    staff.lastName = clean(incoming.lastName);
    staff.gender = clean(incoming.gender);
    staff.dateOfBirth = clean(incoming.dateOfBirth);
    staff.bloodGroup = clean(incoming.bloodGroup);
    staff.phone = clean(incoming.phone);
    staff.email = email.toLowerCase(Locale.ROOT);
    staff.address = clean(incoming.address);
    staff.city = clean(incoming.city);
    staff.state = clean(incoming.state);
    staff.pincode = clean(incoming.pincode);
    staff.aadhaar = clean(incoming.aadhaar);
    staff.pan = clean(incoming.pan).toUpperCase(Locale.ROOT);
    staff.department = required(incoming.department, "Department");
    staff.designation = required(incoming.designation, "Designation");
    staff.category = clean(incoming.category);
    if (staff.category.isEmpty()) staff.category = staff.department;
    staff.workforce = workforce(incoming.workforce, staff.department, staff.designation);
    staff.campus = required(incoming.campus, "Campus");
    staff.reportingManager = clean(incoming.reportingManager);
    staff.joiningDate = joining;
    staff.employmentType = employment(incoming.employmentType);
    staff.status = status(incoming.status);
    staff.qualification = clean(incoming.qualification);
    staff.experienceYears = clean(incoming.experienceYears);
    staff.emergencyName = clean(incoming.emergencyName);
    staff.emergencyPhone = clean(incoming.emergencyPhone);
    staff.bankAccount = clean(incoming.bankAccount);
    staff.ifsc = clean(incoming.ifsc).toUpperCase(Locale.ROOT);
  }

  private String workforce(String requested, String department, String designation) {
    String cleaned = clean(requested);
    if (cleaned.equalsIgnoreCase("Teaching") || cleaned.equalsIgnoreCase("Teaching Faculty") || cleaned.equalsIgnoreCase("Academic")) {
      return "Teaching";
    }
    if (cleaned.equalsIgnoreCase("Support") || cleaned.toLowerCase(Locale.ROOT).contains("non-teaching") || cleaned.toLowerCase(Locale.ROOT).contains("non teaching")) {
      return "Support";
    }
    String blob = (department + " " + designation).toLowerCase(Locale.ROOT);
    if (blob.contains("teacher") || blob.contains("faculty") || blob.contains("lecturer")
        || blob.contains("mathematics") || blob.contains("science") || blob.contains("language") || blob.contains("social")) {
      return "Teaching";
    }
    return "Support";
  }

  private String employment(String value) {
    String cleaned = clean(value);
    if (cleaned.isEmpty()) return "Full Time";
    for (String option : EMPLOYMENT_TYPES) {
      if (option.equalsIgnoreCase(cleaned)) return option;
    }
    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Employment type must be Full Time, Part Time, or Contract.");
  }

  private String status(String value) {
    String cleaned = clean(value);
    if (cleaned.isEmpty() || cleaned.equalsIgnoreCase("ACTIVE")) return "Active";
    for (String option : STATUSES) {
      if (option.equalsIgnoreCase(cleaned)) return option;
    }
    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status must be Active, Probation, Notice, or Inactive.");
  }

  private String required(String value, String label) {
    String cleaned = clean(value);
    if (cleaned.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, label + " is required.");
    }
    if (cleaned.length() > 160) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, label + " is too long.");
    }
    return cleaned;
  }

  private String clean(String value) {
    return value == null ? "" : value.trim().replaceAll("\\s+", " ");
  }

  private String cell(String value) {
    String text = value == null ? "" : value;
    if (text.contains(",") || text.contains("\"") || text.contains("\n")) {
      return "\"" + text.replace("\"", "\"\"") + "\"";
    }
    return text;
  }
}

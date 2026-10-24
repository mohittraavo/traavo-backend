package com.travo.tcms.platform.service;

import com.travo.tcms.platform.entity.AdmissionApplicationEntity;
import com.travo.tcms.platform.entity.StudentEntity;
import com.travo.tcms.platform.repository.AdmissionApplicationRepository;
import com.travo.tcms.platform.repository.StudentRepository;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AdmissionApplicationService {
  public static final List<String> STAGES = List.of("Draft", "Submitted", "Under Review", "Approved", "Admitted", "Enrolled", "Rejected");
  public static final List<String> SOURCES = List.of("Walk-in", "Website", "Referral", "Campus Fair", "Sibling");
  public static final List<String> GENDERS = List.of("Male", "Female", "Other");
  public static final List<String> RELATIONSHIPS = List.of("Father", "Mother", "Guardian", "Other");
  public static final List<String> DOCUMENTS = List.of("Birth Certificate", "Transfer Certificate", "Aadhaar", "Report Card", "Address Proof");
  public static final List<String> VERIFICATION = List.of("Pending", "Verified", "Rejected");

  private final AdmissionApplicationRepository repository;
  private final StudentRepository students;

  public AdmissionApplicationService(AdmissionApplicationRepository repository, StudentRepository students) {
    this.repository = repository;
    this.students = students;
  }

  public List<AdmissionApplicationEntity> list() {
    return repository.findAllByOrderByApplicationNoDesc();
  }

  public Map<String, Object> options() {
    Map<String, Object> body = new LinkedHashMap<>();
    body.put("stages", STAGES);
    body.put("sources", SOURCES);
    body.put("genders", GENDERS);
    body.put("relationships", RELATIONSHIPS);
    body.put("documents", DOCUMENTS);
    body.put("verification", VERIFICATION);
    body.put("classes", List.of("Nursery", "LKG", "UKG", "Grade 1", "Grade 2", "Grade 3", "Grade 4", "Grade 5", "Grade 6", "Grade 7", "Grade 8", "Grade 9", "Grade 10", "Grade 11", "Grade 12"));
    body.put("sections", List.of("A", "B", "C"));
    body.put("years", List.of("2025-2026", "2026-2027", "2027-2028"));
    return body;
  }

  public Map<String, Integer> summary() {
    List<AdmissionApplicationEntity> rows = list();
    Map<String, Integer> counts = new LinkedHashMap<>();
    counts.put("total", rows.size());
    counts.put("review", (int) rows.stream().filter(row -> Set.of("Submitted", "Under Review").contains(row.stage)).count());
    counts.put("approved", (int) rows.stream().filter(row -> "Approved".equals(row.stage)).count());
    counts.put("converted", (int) rows.stream().filter(row -> "Enrolled".equals(row.stage)).count());
    return counts;
  }

  public AdmissionApplicationEntity save(AdmissionApplicationEntity incoming, boolean draft) {
    AdmissionApplicationEntity row = incoming.id == null
        ? new AdmissionApplicationEntity()
        : repository.findById(incoming.id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Application not found."));
    boolean enrolled = "Enrolled".equals(row.stage);
    row.applicationNo = clean(incoming.applicationNo);
    if (row.applicationNo.isEmpty()) row.applicationNo = nextNumber();
    row.applicationDate = clean(incoming.applicationDate);
    if (row.applicationDate.isEmpty()) row.applicationDate = LocalDate.now().toString();
    row.academicYear = fallback(incoming.academicYear, "2026-2027");
    row.campus = required(incoming.campus, "Campus");
    row.applyingClass = required(incoming.applyingClass, "Applying class");
    row.applyingSection = fallback(incoming.applyingSection, "A");
    row.admissionSource = fallback(incoming.admissionSource, "Walk-in");
    row.firstName = required(incoming.firstName, "First name");
    row.middleName = clean(incoming.middleName);
    row.lastName = required(incoming.lastName, "Last name");
    row.gender = fallback(incoming.gender, "Male");
    row.dateOfBirth = clean(incoming.dateOfBirth);
    row.bloodGroup = clean(incoming.bloodGroup);
    row.fatherName = clean(incoming.fatherName);
    row.motherName = clean(incoming.motherName);
    row.guardianRelationship = fallback(incoming.guardianRelationship, "Father");
    row.phone = required(incoming.phone, "Contact phone");
    row.email = clean(incoming.email);
    row.address = clean(incoming.address);
    row.city = clean(incoming.city);
    row.state = clean(incoming.state);
    row.pincode = clean(incoming.pincode);
    row.previousSchool = clean(incoming.previousSchool);
    row.previousClass = clean(incoming.previousClass);
    row.previousGrade = clean(incoming.previousGrade);
    row.entranceScore = clean(incoming.entranceScore);
    row.remarks = clean(incoming.remarks);
    row.documentType = clean(incoming.documentType);
    row.documentNumber = clean(incoming.documentNumber);
    row.verificationStatus = fallback(incoming.verificationStatus, "Pending");
    String stage = clean(incoming.stage);
    if (enrolled) stage = "Enrolled";
    else if (!STAGES.contains(stage)) stage = draft ? "Draft" : "Submitted";
    else if (draft) stage = "Draft";
    else if ("Draft".equals(stage)) stage = "Submitted";
    row.stage = stage;
    return repository.save(row);
  }

  public AdmissionApplicationEntity review(Long id, String stage, String score) {
    AdmissionApplicationEntity row = repository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Application not found."));
    if ("Enrolled".equals(row.stage)) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Converted applications stay enrolled.");
    }
    if (!STAGES.contains(stage) || "Draft".equals(stage) || "Enrolled".equals(stage)) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Choose a review stage.");
    }
    row.stage = stage;
    row.entranceScore = clean(score);
    return repository.save(row);
  }

  public AdmissionApplicationEntity convert(Long id) {
    AdmissionApplicationEntity row = repository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Application not found."));
    if ("Enrolled".equals(row.stage) && row.studentId != null) return row;
    if (!Set.of("Approved", "Admitted").contains(row.stage)) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Approve the application before converting it to a student.");
    }
    StudentEntity student = new StudentEntity();
    student.admissionNo = row.applicationNo;
    student.admissionDate = row.applicationDate;
    student.firstName = row.firstName;
    student.lastName = row.lastName;
    student.classGrade = row.applyingClass;
    student.section = row.applyingSection;
    student.academicSession = row.academicYear;
    student.dob = row.dateOfBirth;
    student.gender = row.gender;
    student.bloodGroup = row.bloodGroup;
    student.fatherName = row.fatherName;
    student.fatherMobile = row.phone;
    student.motherName = row.motherName;
    student.guardianName = row.fatherName;
    student.guardianRelation = row.guardianRelationship;
    student.guardianPhone = row.phone;
    student.guardianEmail = row.email;
    student.residentialAddress = row.address;
    student.city = row.city;
    student.state = row.state;
    student.pincode = row.pincode;
    student.previousSchoolName = row.previousSchool;
    student.previousClassPassed = row.previousClass;
    student.admissionQuota = row.admissionSource;
    student.feeStatus = "Due";
    student.attendancePercent = "0";
    StudentEntity saved = students.save(student);
    row.studentId = saved.id;
    row.stage = "Enrolled";
    return repository.save(row);
  }

  public void delete(Long id) {
    if (!repository.existsById(id)) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Application not found.");
    repository.deleteById(id);
  }

  public String exportCsv() {
    StringBuilder csv = new StringBuilder("ApplicationNo,Applicant,ApplyingClass,Parent,Phone,Score,Stage,Campus,AcademicYear\n");
    for (AdmissionApplicationEntity row : list()) {
      csv.append(cell(row.applicationNo)).append(',')
          .append(cell((row.firstName + " " + clean(row.lastName)).trim())).append(',')
          .append(cell(row.applyingClass)).append(',')
          .append(cell(row.fatherName)).append(',')
          .append(cell(row.phone)).append(',')
          .append(cell(row.entranceScore)).append(',')
          .append(cell(row.stage)).append(',')
          .append(cell(row.campus)).append(',')
          .append(cell(row.academicYear)).append('\n');
    }
    return csv.toString();
  }

  private String nextNumber() {
    int year = LocalDate.now().getYear();
    int seq = (int) repository.count() + 100;
    String candidate;
    do {
      seq += 1;
      candidate = "APP-" + year + "-" + seq;
    } while (repository.existsByApplicationNoIgnoreCase(candidate));
    return candidate;
  }

  private String required(String value, String label) {
    String cleaned = clean(value);
    if (cleaned.isEmpty()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, label + " is required.");
    return cleaned;
  }

  private String fallback(String value, String fallback) {
    String cleaned = clean(value);
    return cleaned.isEmpty() ? fallback : cleaned;
  }

  private String clean(String value) {
    return value == null ? "" : value.trim();
  }

  private String cell(String value) {
    String text = clean(value).replace("\"", "\"\"");
    return text.contains(",") || text.contains("\"") ? "\"" + text + "\"" : text;
  }
}

package com.travo.tcms.platform.service;

import com.travo.tcms.platform.entity.StudentEntity;
import com.travo.tcms.platform.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class StudentService {
  @Autowired
  private StudentRepository repo;

  public List<StudentEntity> list(Map<String, String> filters) {
    List<StudentEntity> all = repo.findAll();
    if (filters == null || filters.isEmpty()) {
      return all;
    }
    return all.stream().filter(student -> matches(student, filters)).collect(Collectors.toList());
  }

  public Set<String> existingAdmissionNos() {
    return repo.findAdmissionNumbers().stream()
        .map(value -> value.trim().toLowerCase(Locale.ROOT))
        .filter(value -> !value.isBlank())
        .collect(Collectors.toSet());
  }

  public Optional<StudentEntity> get(Long id) {
    return repo.findById(id);
  }

  public StudentEntity create(StudentEntity student) {
    return repo.save(student);
  }

  public StudentEntity update(Long id, StudentEntity student) {
    return repo.findById(id).map(existing -> {
      student.id = existing.id;
      return repo.save(student);
    }).orElseGet(() -> repo.save(student));
  }

  public void delete(Long id) {
    repo.deleteById(id);
  }

  public List<StudentEntity> bulkSave(List<StudentEntity> list) {
    return repo.saveAll(list);
  }

  private boolean matches(StudentEntity student, Map<String, String> filters) {
    for (var entry : filters.entrySet()) {
      String key = entry.getKey();
      String value = entry.getValue();
      if (value == null || value.isBlank()) {
        continue;
      }
      String needle = value.toLowerCase(Locale.ROOT).trim();
      boolean ok = switch (key) {
        case "admissionNo" -> contains(student.admissionNo, needle) || equalsIgnore(student.admissionNo, needle);
        case "firstName" -> contains(student.firstName, needle);
        case "lastName" -> contains(student.lastName, needle);
        case "classGrade" -> equalsIgnore(student.classGrade, needle);
        case "section" -> equalsIgnore(student.section, needle);
        case "gender" -> equalsIgnore(student.gender, needle);
        case "city" -> equalsIgnore(student.city, needle);
        case "academicSession" -> contains(student.academicSession, needle);
        case "q" -> contains(student.firstName, needle)
            || contains(student.lastName, needle)
            || contains(student.admissionNo, needle)
            || contains(student.classGrade, needle)
            || contains(student.section, needle)
            || contains(student.fatherName, needle)
            || contains(student.fatherMobile, needle);
        default -> true;
      };
      if (!ok) {
        return false;
      }
    }
    return true;
  }

  private static boolean contains(String source, String needle) {
    return source != null && source.toLowerCase(Locale.ROOT).contains(needle);
  }

  private static boolean equalsIgnore(String source, String needle) {
    return source != null && source.trim().equalsIgnoreCase(needle);
  }
}

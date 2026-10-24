package com.travo.tcms.platform.config;

import com.travo.tcms.platform.entity.AcademicClassEntity;
import com.travo.tcms.platform.entity.SchoolBranchEntity;
import com.travo.tcms.platform.entity.ClassSectionEntity;
import com.travo.tcms.platform.entity.CurriculumSubjectEntity;
import com.travo.tcms.platform.entity.SchoolProfileEntity;
import com.travo.tcms.platform.entity.StaffDepartmentEntity;
import com.travo.tcms.platform.repository.AcademicClassRepository;
import com.travo.tcms.platform.repository.SchoolBranchRepository;
import com.travo.tcms.platform.repository.ClassSectionRepository;
import com.travo.tcms.platform.repository.CurriculumSubjectRepository;
import com.travo.tcms.platform.repository.SchoolProfileRepository;
import com.travo.tcms.platform.repository.StaffDepartmentRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
public class AcademicSetupSeeder {
  private final SchoolProfileRepository profiles;
  private final SchoolBranchRepository campuses;
  private final AcademicClassRepository classes;
  private final ClassSectionRepository sections;
  private final CurriculumSubjectRepository subjects;
  private final StaffDepartmentRepository departments;

  public AcademicSetupSeeder(
      SchoolProfileRepository profiles,
      SchoolBranchRepository campuses,
      AcademicClassRepository classes,
      ClassSectionRepository sections,
      CurriculumSubjectRepository subjects,
      StaffDepartmentRepository departments
  ) {
    this.profiles = profiles;
    this.campuses = campuses;
    this.classes = classes;
    this.sections = sections;
    this.subjects = subjects;
    this.departments = departments;
  }

  @PostConstruct
  void seed() {
    if (profiles.count() == 0) {
      SchoolProfileEntity profile = new SchoolProfileEntity();
      profile.schoolName = "Delhi Public Academy";
      profile.schoolCode = "DPA-N0";
      profile.schoolType = "K-12 (Kindergarten to Grade 12)";
      profile.contactPerson = "Dr. Rajeshwari Sen";
      profile.contactEmail = "principal@dpa.edu.in";
      profile.telephone = "+91 11 2689 1234";
      profile.website = "https://www.dpa.edu.in";
      profile.postalAddress = "Sector C, Pocket 2, Vasant Kunj, New Delhi";
      profile.country = "India";
      profile.state = "Delhi";
      profile.city = "New Delhi";
      profile.postalCode = "110070";
      profile.academicSession = "2026 - 2027 (Active Session)";
      profile.schoolStatus = "Active Operational";
      profiles.save(profile);
    }

    SchoolBranchEntity main = campuses.findAll().stream().filter(item -> item.mainCampus).findFirst().orElse(null);
    if (campuses.count() == 0) {
      main = campus("Main Campus (Vasant Kunj)", "Sector C, Pocket 2, Vasant Kunj, New Delhi", "DPA-VK", "+91 11 2689 1234", true);
      campus("Junior Wing (Dwarka)", "Sector 6, Dwarka, New Delhi", "DPA-DW", "+91 11 2808 5678", false);
    }
    if (main == null) {
      main = campuses.findAll().stream().findFirst().orElse(null);
    }
    if (main == null) {
      seedDepartments();
      return;
    }
    if (classes.count() > 0) {
      ensureSections();
      if (subjects.count() == 0) seedSubjects();
      seedDepartments();
      return;
    }

    String[][] grades = {
        {"Nursery", "CL-NUR"},
        {"LKG", "CL-LKG"},
        {"UKG", "CL-UKG"},
        {"Class 1", "CL-01"},
        {"Class 2", "CL-02"},
        {"Class 3", "CL-03"},
        {"Class 4", "CL-04"},
        {"Class 5", "CL-05"},
        {"Class 6", "CL-06"},
        {"Class 7", "CL-07"},
        {"Grade 8", "CL-08"},
        {"Grade 9", "CL-09"},
        {"Grade 10", "CL-10"},
        {"Grade 11", "CL-11"},
        {"Grade 12", "CL-12"}
    };
    AcademicClassEntity grade8 = null;
    AcademicClassEntity grade9 = null;
    AcademicClassEntity grade10 = null;
    int order = 1;
    for (String[] grade : grades) {
      AcademicClassEntity item = grade(main.id, grade[0], grade[1], order++);
      if ("Grade 8".equals(grade[0])) grade8 = item;
      if ("Grade 9".equals(grade[0])) grade9 = item;
      if ("Grade 10".equals(grade[0])) grade10 = item;
    }
    if (sections.count() == 0 && grade8 != null && grade9 != null && grade10 != null) {
      section(main.id, grade8.id, "Section A", "SEC-8A", "Dr. Rameshwar Pandey (Social Studies)");
      section(main.id, grade8.id, "Section B", "SEC-8B", "Mr. Vikram Joshi (Mathematics)");
      section(main.id, grade9.id, "Section A", "SEC-9A", "Ms. Neha Kapoor (Science)");
      section(main.id, grade9.id, "Section B", "SEC-9B", "Ms. Anjali Mehra (English)");
      section(main.id, grade10.id, "Section A", "SEC-10A", "Mr. Arjun Malhotra (Physics)");
    }
    if (subjects.count() == 0) seedSubjects();
    ensureSections();
    seedDepartments();
  }

  private void seedSubjects() {
    subject("Mathematics", "SUB-MATH", 1);
    subject("Science", "SUB-SCI", 2);
    subject("Physics", "SUB-PHY", 3);
    subject("Chemistry", "SUB-CHEM", 4);
    subject("Biology", "SUB-BIO", 5);
    subject("English Literature", "SUB-ENG", 6);
    subject("Social Studies", "SUB-SOC", 7);
    subject("Computer Science", "SUB-CSC", 8);
    subject("Hindi", "SUB-HIN", 9);
  }

  private void ensureSections() {
    for (AcademicClassEntity grade : classes.findAll()) {
      boolean hasA = sections.findAll().stream().anyMatch(item -> grade.id.equals(item.classId) && "Section A".equalsIgnoreCase(item.name));
      boolean hasB = sections.findAll().stream().anyMatch(item -> grade.id.equals(item.classId) && "Section B".equalsIgnoreCase(item.name));
      if (!hasA) section(grade.campusId, grade.id, "Section A", sectionCode(grade.code, "A"), "");
      if (!hasB) section(grade.campusId, grade.id, "Section B", sectionCode(grade.code, "B"), "");
    }
  }

  private String sectionCode(String classCode, String letter) {
    String base = classCode == null ? "X" : classCode.replaceFirst("(?i)^CL-?", "");
    String candidate = "SEC-" + base + letter;
    int suffix = 2;
    while (sections.existsByCodeIgnoreCase(candidate)) {
      candidate = "SEC-" + base + letter + suffix;
      suffix += 1;
    }
    return candidate;
  }

  private void seedDepartments() {
    if (departments.count() > 0) return;
    department("Mathematics Department", "DEP-MATH");
    department("Science Department", "DEP-SCI");
    department("Languages Department", "DEP-LANG");
    department("Social Studies", "DEP-SOC");
    department("Administration & Finance", "DEP-ADMIN");
    department("Operations & Facilities", "DEP-OPS");
  }

  private SchoolBranchEntity campus(String name, String address, String code, String phone, boolean main) {
    SchoolBranchEntity item = new SchoolBranchEntity();
    item.name = name;
    item.address = address;
    item.code = code;
    item.phone = phone;
    item.mainCampus = main;
    return campuses.save(item);
  }

  private AcademicClassEntity grade(Long campusId, String name, String code, int order) {
    AcademicClassEntity item = new AcademicClassEntity();
    item.academicYear = "2026-2027";
    item.campusId = campusId;
    item.name = name;
    item.code = code;
    item.displayOrder = order;
    item.status = "Active";
    return classes.save(item);
  }

  private void section(Long campusId, Long classId, String name, String code, String teacher) {
    ClassSectionEntity item = new ClassSectionEntity();
    item.academicYear = "2026-2027";
    item.campusId = campusId;
    item.classId = classId;
    item.name = name;
    item.code = code;
    item.capacity = 35;
    item.status = "Active";
    item.teacher = teacher;
    sections.save(item);
  }

  private void subject(String name, String code, int order) {
    CurriculumSubjectEntity item = new CurriculumSubjectEntity();
    item.academicYear = "2026-2027";
    item.name = name;
    item.code = code;
    item.subjectType = "Core Subject";
    item.displayOrder = order;
    item.status = "Active";
    subjects.save(item);
  }

  private void department(String name, String code) {
    StaffDepartmentEntity item = new StaffDepartmentEntity();
    item.name = name;
    item.code = code;
    departments.save(item);
  }
}

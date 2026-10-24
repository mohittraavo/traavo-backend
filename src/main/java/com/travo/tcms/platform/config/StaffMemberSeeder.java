package com.travo.tcms.platform.config;

import com.travo.tcms.platform.entity.StaffMemberEntity;
import com.travo.tcms.platform.repository.StaffMemberRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
public class StaffMemberSeeder {
  private final StaffMemberRepository repository;

  public StaffMemberSeeder(StaffMemberRepository repository) {
    this.repository = repository;
  }

  @PostConstruct
  void seed() {
    if (repository.count() > 0) return;
    add("ERP-T-002", "Dr.", "Rameshwar", "Pandey", "Social Studies", "Faculty", "Faculty", "+91 94150 99887", "rameshwar.p@traavo.edu", "Principal");
    add("ERP-T-055", "Mr.", "Arvind", "Swaminathan", "Science", "Assistant Teacher", "Faculty", "+91 98450 67890", "arvind.s@traavo.edu", "Principal");
    add("ERP-A-012", "Mr.", "Manoj", "Khatri", "Administration", "Finance Controller", "Administration", "+91 98112 55667", "manoj.k@traavo.edu", "Principal");
    add("ERP-T-042", "Mrs.", "Sunita", "Deshmukh", "Mathematics", "Senior Faculty & Head of Dept", "Faculty", "+91 98230 45678", "sunita.d@traavo.edu", "Principal");
    add("ERP-A-024", "Ms.", "Geeta", "Nair", "Operations", "Admissions Officer", "Operations", "+91 99481 22334", "geeta.n@traavo.edu", "Principal");
    add("ERP-T-089", "Ms.", "Rebecca", "D'Souza", "Languages", "Senior Faculty", "Faculty", "+91 98282 33445", "rebecca.d@traavo.edu", "Principal");
  }

  private void add(String code, String salutation, String first, String last, String department, String designation, String category, String phone, String email, String manager) {
    StaffMemberEntity staff = new StaffMemberEntity();
    staff.employeeCode = code;
    staff.salutation = salutation;
    staff.firstName = first;
    staff.lastName = last;
    staff.department = department;
    staff.designation = designation;
    staff.category = category;
    staff.workforce = "Teaching";
    staff.campus = "Main Campus";
    staff.phone = phone;
    staff.email = email;
    staff.joiningDate = "2026-10-18";
    staff.employmentType = "Full Time";
    staff.status = "Active";
    staff.reportingManager = manager;
    staff.gender = salutation.equals("Mrs.") || salutation.equals("Ms.") ? "Female" : "Male";
    repository.save(staff);
  }
}

package com.travo.tcms.platform.config;

import com.travo.tcms.platform.entity.StaffMemberEntity;
import com.travo.tcms.platform.entity.TeachingAssignmentEntity;
import com.travo.tcms.platform.repository.StaffMemberRepository;
import com.travo.tcms.platform.repository.TeachingAssignmentRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(100)
public class TeachingAssignmentSeeder implements ApplicationRunner {
  private final TeachingAssignmentRepository assignments;
  private final StaffMemberRepository staff;

  public TeachingAssignmentSeeder(TeachingAssignmentRepository assignments, StaffMemberRepository staff) {
    this.assignments = assignments;
    this.staff = staff;
  }

  @Override
  public void run(ApplicationArguments args) {
    if (assignments.count() > 0) return;
    add("ERP-T-002", "Grade 8", "Section B", "Social Studies", 4, 4, 0, true, 26);
    add("ERP-T-055", "Grade 8", "Section A", "Physics", 5, 4, 1, false, 28);
    add("ERP-T-042", "Grade 8", "Section A", "Mathematics", 6, 5, 1, true, 30);
    add("ERP-T-042", "Grade 8", "Section B", "Mathematics", 6, 5, 1, false, 30);
    add("ERP-T-089", "Grade 8", "Section A", "English Literature", 5, 5, 0, false, 30);
  }

  private void add(String code, String className, String section, String subject, int weekly, int theory, int practical, boolean classTeacher, int capacity) {
    StaffMemberEntity person = staff.findByEmployeeCodeIgnoreCase(code).orElse(null);
    if (person == null) return;
    TeachingAssignmentEntity item = new TeachingAssignmentEntity();
    item.staffId = person.id;
    item.academicYear = "2026-2027";
    item.campus = person.campus == null ? "Main Campus" : person.campus;
    item.dutyType = "Core";
    item.className = className;
    item.sectionName = section;
    item.subject = subject;
    item.weeklyPeriods = weekly;
    item.theoryPeriods = theory;
    item.practicalPeriods = practical;
    item.classTeacher = classTeacher;
    item.effectiveFrom = "2026-04-01";
    item.effectiveTo = "2027-03-31";
    item.notes = "";
    item.weeklyCapacity = capacity;
    item.status = "Active";
    assignments.save(item);
  }
}

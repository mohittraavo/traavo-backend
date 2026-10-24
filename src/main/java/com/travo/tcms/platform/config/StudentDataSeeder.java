package com.travo.tcms.platform.config;

import com.travo.tcms.platform.entity.StudentEntity;
import com.travo.tcms.platform.repository.StudentRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
public class StudentDataSeeder {
  private final StudentRepository repository;

  public StudentDataSeeder(StudentRepository repository) {
    this.repository = repository;
  }

  @PostConstruct
  void seed() {
    seed("1023", "2018-04-12", "Aarav", "Singh", "8", "A", "12", "Male", "Rajesh Singh", "9876543210");
    seed("1024", "2018-04-15", "Ananya", "Mehta", "8", "A", "13", "Female", "Rohan Mehta", "9876501234");
    seed("1101", "2017-06-01", "Vihaan", "Kapoor", "9", "B", "04", "Male", "Amit Kapoor", "9810011223");
    seed("1102", "2017-06-03", "Ishita", "Sharma", "9", "B", "05", "Female", "Nikhil Sharma", "9822098765");
    seed("1205", "2019-03-21", "Kabir", "Verma", "7", "C", "08", "Male", "Suresh Verma", "9900112233");
    seed("1206", "2019-03-22", "Myra", "Gupta", "7", "A", "09", "Female", "Pankaj Gupta", "9911223344");
    seed("1308", "2016-04-08", "Reyansh", "Nair", "10", "A", "01", "Male", "Arun Nair", "9933445566");
    seed("1309", "2016-04-10", "Diya", "Iyer", "10", "A", "02", "Female", "Karthik Iyer", "9944556677");
    seed("1412", "2020-04-02", "Advait", "Khan", "6", "B", "21", "Male", "Imran Khan", "9955667788");
    seed("1413", "2020-04-05", "Saanvi", "Reddy", "6", "B", "22", "Female", "Prakash Reddy", "9966778899");
    seed("1501", "2015-05-18", "Arjun", "Patel", "11", "A", "07", "Male", "Mehul Patel", "9977889900");
    seed("1502", "2015-05-19", "Kiara", "Joshi", "11", "C", "08", "Female", "Neeraj Joshi", "9988990011");
    backfill();
  }

  private void backfill() {
    for (StudentEntity student : repository.findAll()) {
      boolean dirty = false;
      if (isBlank(student.guardianName) && !isBlank(student.fatherName)) {
        student.guardianName = student.fatherName;
        dirty = true;
      }
      if (isBlank(student.guardianPhone) && !isBlank(student.fatherMobile)) {
        student.guardianPhone = student.fatherMobile;
        dirty = true;
      }
      if (isBlank(student.apaarId) && student.admissionNo != null) {
        String digits = ("12" + student.admissionNo + "0000000000").replaceAll("[^0-9]", "");
        student.apaarId = digits.length() >= 12 ? digits.substring(0, 12) : (digits + "000000000000").substring(0, 12);
        dirty = true;
      }
      if (isBlank(student.feeStatus)) {
        long n = student.id == null ? 1 : student.id;
        student.feeStatus = n % 3 == 0 ? "Due" : "Paid";
        dirty = true;
      }
      if (isBlank(student.attendancePercent)) {
        long n = student.id == null ? 1 : student.id;
        student.attendancePercent = String.valueOf(88 + (n % 12));
        dirty = true;
      }
      if (dirty) repository.save(student);
    }
  }

  private boolean isBlank(String value) {
    return value == null || value.trim().isEmpty();
  }

  private void seed(String admissionNo, String admissionDate, String firstName, String lastName,
                    String classGrade, String section, String rollNo, String gender,
                    String fatherName, String fatherMobile) {
    if (repository.existsByAdmissionNoIgnoreCase(admissionNo)) {
      return;
    }
    StudentEntity student = new StudentEntity();
    student.admissionNo = admissionNo;
    student.admissionDate = admissionDate;
    student.firstName = firstName;
    student.lastName = lastName;
    student.classGrade = classGrade;
    student.section = section;
    student.rollNo = rollNo;
    student.academicSession = "2026-27";
    student.dob = "2013-01-15";
    student.gender = gender;
    student.bloodGroup = "O+";
    student.motherTongue = "Hindi";
    student.mediumOfInstruction = "English";
    student.category = "General";
    student.fatherName = fatherName;
    student.fatherMobile = fatherMobile;
    student.guardianName = fatherName;
    student.guardianPhone = fatherMobile;
    student.apaarId = ("12" + admissionNo + "0000000000").replaceAll("[^0-9]", "").substring(0, 12);
    student.feeStatus = Integer.parseInt(admissionNo.replaceAll("[^0-9]", "") + "0") % 3 == 0 ? "Due" : "Paid";
    student.attendancePercent = String.valueOf(88 + (Integer.parseInt(admissionNo.replaceAll("[^0-9]", "")) % 12));
    student.city = "Noida";
    student.state = "Uttar Pradesh";
    repository.save(student);
  }
}

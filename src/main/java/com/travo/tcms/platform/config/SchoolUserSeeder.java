package com.travo.tcms.platform.config;

import com.travo.tcms.platform.entity.SchoolUserEntity;
import com.travo.tcms.platform.repository.SchoolUserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
public class SchoolUserSeeder {
  private final SchoolUserRepository repository;
  private final PasswordHasher passwords;

  public SchoolUserSeeder(SchoolUserRepository repository, PasswordHasher passwords) {
    this.repository = repository;
    this.passwords = passwords;
  }

  @PostConstruct
  void seed() {
    seedUser("admin@greenvalley.edu", "Rohan Mehta", "ADMIN");
    seedUser("super@greenvalley.edu", "Aditi Sharma", "SUPER_ADMIN");
    seedUser("teacher@greenvalley.edu", "Riya Kapoor", "TEACHER");
    seedUser("student@greenvalley.edu", "Aarav Singh", "STUDENT");
  }

  private void seedUser(String email, String fullName, String role) {
    if (repository.findByEmailIgnoreCase(email).isPresent()) {
      return;
    }
    SchoolUserEntity user = new SchoolUserEntity();
    user.email = email;
    user.password = passwords.hash("Admin@123");
    user.fullName = fullName;
    user.role = role;
    user.schoolId = 1L;
    user.schoolName = "Green Valley Group of Schools";
    user.status = "ACTIVE";
    repository.save(user);
  }
}

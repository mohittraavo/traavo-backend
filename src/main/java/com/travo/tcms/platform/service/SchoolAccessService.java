package com.travo.tcms.platform.service;

import com.travo.tcms.platform.config.PasswordHasher;
import com.travo.tcms.platform.entity.SchoolEntity;
import com.travo.tcms.platform.entity.SchoolUserEntity;
import com.travo.tcms.platform.repository.SchoolUserRepository;
import java.security.SecureRandom;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class SchoolAccessService {
  private static final String PASSWORD_ALPHABET = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz23456789";
  private static final int PASSWORD_LENGTH = 12;

  private final SchoolUserRepository users;
  private final PasswordHasher passwords;
  private final SecureRandom random = new SecureRandom();

  public SchoolAccessService(SchoolUserRepository users, PasswordHasher passwords) {
    this.users = users;
    this.passwords = passwords;
  }

  public SchoolAccess provision(SchoolEntity school) {
    String email = recipient(school);
    if (!email.contains("@")) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Add a school admin email before sending school access.");
    }
    String name = adminName(school);
    SchoolUserEntity existing = users.findByEmailIgnoreCase(email).orElse(null);
    if (existing != null) {
      if (existing.schoolId != null && school.id != null && !existing.schoolId.equals(school.id)) {
        throw new ResponseStatusException(HttpStatus.CONFLICT, "This email already has access to another school.");
      }
      existing.schoolId = school.id;
      existing.schoolName = school.schoolName;
      existing.fullName = existing.fullName == null || existing.fullName.isBlank() ? name : existing.fullName;
      existing.role = existing.role == null || existing.role.isBlank() ? "ADMIN" : existing.role;
      existing.status = "ACTIVE";
      users.save(existing);
      return new SchoolAccess(email, null);
    }

    String temporaryPassword = temporaryPassword();
    SchoolUserEntity user = new SchoolUserEntity();
    user.schoolId = school.id;
    user.schoolName = school.schoolName;
    user.fullName = name;
    user.email = email;
    user.password = passwords.hash(temporaryPassword);
    user.role = "ADMIN";
    user.status = "ACTIVE";
    users.save(user);
    return new SchoolAccess(email, temporaryPassword);
  }

  private String recipient(SchoolEntity school) {
    String admin = clean(school.schoolAdminEmail);
    if (admin.contains("@")) {
      return admin;
    }
    String contact = school.contact == null ? "" : clean(school.contact.schoolEmail);
    if (contact.contains("@")) {
      return contact;
    }
    String schoolEmail = clean(school.email);
    if (schoolEmail.contains("@")) {
      return schoolEmail;
    }
    String principal = school.principal == null ? "" : clean(school.principal.email);
    return principal;
  }

  private String adminName(SchoolEntity school) {
    String name = clean(school.schoolAdminName);
    if (!name.isEmpty()) {
      return name;
    }
    if (school.principal != null && !clean(school.principal.principalName).isEmpty()) {
      return clean(school.principal.principalName);
    }
    return "School Admin";
  }

  private String temporaryPassword() {
    StringBuilder password = new StringBuilder(PASSWORD_LENGTH);
    for (int i = 0; i < PASSWORD_LENGTH; i++) {
      password.append(PASSWORD_ALPHABET.charAt(random.nextInt(PASSWORD_ALPHABET.length())));
    }
    return password.toString();
  }

  private static String clean(String value) {
    return value == null ? "" : value.trim();
  }

  public record SchoolAccess(String email, String temporaryPassword) {
  }
}

package com.travo.tcms.platform.config;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class PasswordHasher {
  private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

  public String hash(String rawPassword) {
    return encoder.encode(rawPassword);
  }

  public boolean matches(String rawPassword, String storedPassword) {
    if (rawPassword == null || storedPassword == null || storedPassword.isBlank()) {
      return false;
    }
    if (isHashed(storedPassword)) {
      return encoder.matches(rawPassword, storedPassword);
    }
    return rawPassword.equals(storedPassword);
  }

  public boolean isHashed(String storedPassword) {
    return storedPassword != null
        && (storedPassword.startsWith("$2a$") || storedPassword.startsWith("$2b$") || storedPassword.startsWith("$2y$"));
  }
}

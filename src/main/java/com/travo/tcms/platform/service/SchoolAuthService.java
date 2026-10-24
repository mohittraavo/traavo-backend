package com.travo.tcms.platform.service;

import com.travo.tcms.platform.config.JwtUtil;
import com.travo.tcms.platform.config.PasswordHasher;
import com.travo.tcms.platform.dto.LoginRequest;
import com.travo.tcms.platform.dto.SchoolAuthResponse;
import com.travo.tcms.platform.dto.SchoolAuthUserResponse;
import com.travo.tcms.platform.entity.SchoolSessionEntity;
import com.travo.tcms.platform.entity.SchoolUserEntity;
import com.travo.tcms.platform.repository.SchoolSessionRepository;
import com.travo.tcms.platform.repository.SchoolUserRepository;
import io.jsonwebtoken.Claims;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class SchoolAuthService {
  public static final String AUDIENCE = "school";

  private final SchoolUserRepository userRepository;
  private final SchoolSessionRepository sessionRepository;
  private final JwtUtil jwtUtil;
  private final PasswordHasher passwords;

  public SchoolAuthService(SchoolUserRepository userRepository, SchoolSessionRepository sessionRepository, JwtUtil jwtUtil, PasswordHasher passwords) {
    this.userRepository = userRepository;
    this.sessionRepository = sessionRepository;
    this.jwtUtil = jwtUtil;
    this.passwords = passwords;
  }

  public SchoolAuthResponse login(LoginRequest request) {
    SchoolUserEntity user = userRepository.findByEmailIgnoreCase(request.email())
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));
    if (!passwords.matches(request.password(), user.password)) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
    }
    if (!passwords.isHashed(user.password)) {
      user.password = passwords.hash(request.password());
    }
    if (user.status != null && !"ACTIVE".equalsIgnoreCase(user.status)) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Account is not active");
    }

    user.lastLogin = Instant.now().toString();
    userRepository.save(user);

    SchoolSessionEntity session = new SchoolSessionEntity();
    session.token = UUID.randomUUID().toString();
    session.userId = user.id;
    session.createdAt = Instant.now();
    session.expiresAt = Instant.now().plusSeconds(60L * 60L * 24L);
    sessionRepository.save(session);

    String jwt = jwtUtil.generateToken(session.token, user.id, user.schoolId, user.role, List.of(), AUDIENCE);
    return new SchoolAuthResponse(jwt, toUser(user));
  }

  public SchoolAuthResponse getSession(String token) {
    SchoolUserEntity user = requireUserByToken(token);
    return new SchoolAuthResponse(token, toUser(user));
  }

  @Transactional
  public void logout(String token) {
    Claims claims = jwtUtil.validateAndParse(token);
    requireSchoolAudience(claims);
    sessionRepository.deleteByToken(JwtUtil.extractSessionId(claims));
  }

  public SchoolUserEntity requireUserByToken(String token) {
    Claims claims = jwtUtil.validateAndParse(token);
    requireSchoolAudience(claims);
    String sessionId = JwtUtil.extractSessionId(claims);
    SchoolSessionEntity session = sessionRepository.findByToken(sessionId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid session"));
    if (session.expiresAt.isBefore(Instant.now())) {
      sessionRepository.delete(session);
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Session expired");
    }
    return userRepository.findById(session.userId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
  }

  private void requireSchoolAudience(Claims claims) {
    if (!AUDIENCE.equals(claims.getAudience())) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Platform token cannot access school APIs");
    }
  }

  private SchoolAuthUserResponse toUser(SchoolUserEntity user) {
    return new SchoolAuthUserResponse(user.id, user.schoolId, user.fullName, user.email, user.role, user.schoolName);
  }
}

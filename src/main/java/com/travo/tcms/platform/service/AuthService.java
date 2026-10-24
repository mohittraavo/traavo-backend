package com.travo.tcms.platform.service;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import com.travo.tcms.platform.repository.SessionRepository;
import com.travo.tcms.platform.repository.TcmsUserRepository;
import com.travo.tcms.platform.entity.SessionEntity;
import com.travo.tcms.platform.entity.TcmsUserEntity;
import com.travo.tcms.platform.dto.AuthResponse;
import com.travo.tcms.platform.dto.AuthUserResponse;
import com.travo.tcms.platform.dto.LoginRequest;
import com.travo.tcms.platform.service.RoleService;
import com.travo.tcms.platform.config.JwtUtil;
import com.travo.tcms.platform.config.PasswordHasher;
import io.jsonwebtoken.Claims;

@Service
public class AuthService {

  private final TcmsUserRepository userRepository;
  private final SessionRepository sessionRepository;
  private final RoleService roleService;
  private final JwtUtil jwtUtil;
  private final PasswordHasher passwords;

  public AuthService(TcmsUserRepository userRepository, SessionRepository sessionRepository, RoleService roleService, JwtUtil jwtUtil, PasswordHasher passwords) {
    this.userRepository = userRepository;
    this.sessionRepository = sessionRepository;
    this.roleService = roleService;
    this.jwtUtil = jwtUtil;
    this.passwords = passwords;
  }

  public AuthResponse login(LoginRequest request) {
    TcmsUserEntity user = userRepository.findByEmailIgnoreCase(request.email())
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));
    if (!passwords.matches(request.password(), user.password)) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
    }
    if (!passwords.isHashed(user.password)) {
      user.password = passwords.hash(request.password());
    }

    user.lastLogin = Instant.now().toString();
    userRepository.save(user);

    // Keep session in DB for server-side invalidation/expiry
    SessionEntity session = new SessionEntity();
    session.token = UUID.randomUUID().toString();
    session.userId = user.id;
    session.createdAt = Instant.now();
    session.expiresAt = Instant.now().plusSeconds(60L * 60L * 24L);
    sessionRepository.save(session);

    // compute effective permissions from role
    Map<String, Map<String, Boolean>> rolePermissions = roleService.getRolePermissions();
    Map<String, Boolean> roleMap = rolePermissions.getOrDefault(user.roleName, Map.of());
    List<String> enabled = roleMap.entrySet().stream().filter(Map.Entry::getValue).map(Map.Entry::getKey).collect(Collectors.toList());

    String jwt = jwtUtil.generateToken(session.token, user.id, user.tenantId, user.roleName, enabled);
    return new AuthResponse(jwt, toAuthUser(user, enabled));
  }

  public AuthResponse getSession(String token) {
    Claims claims = jwtUtil.validateAndParse(token);
    String sessionId = JwtUtil.extractSessionId(claims);
    // validate session still exists and not expired
    SessionEntity session = sessionRepository.findByToken(sessionId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid session"));
    if (session.expiresAt.isBefore(Instant.now())) {
      sessionRepository.delete(session);
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Session expired");
    }
    TcmsUserEntity user = userRepository.findById(session.userId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

    List<String> perms = JwtUtil.extractPermissions(claims);
    return new AuthResponse(token, toAuthUser(user, perms));
  }

  public void logout(String token) {
    // delete session by session id stored in token
    Claims claims = jwtUtil.validateAndParse(token);
    String sessionId = JwtUtil.extractSessionId(claims);
    sessionRepository.deleteByToken(sessionId);
  }

  public TcmsUserEntity requireUserByToken(String token) {
    Claims claims = jwtUtil.validateAndParse(token);
    if ("school".equals(claims.getAudience())) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "School token cannot access platform APIs");
    }
    String sessionId = JwtUtil.extractSessionId(claims);
    SessionEntity session = sessionRepository.findByToken(sessionId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid session"));
    if (session.expiresAt.isBefore(Instant.now())) {
      sessionRepository.delete(session);
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Session expired");
    }
    TcmsUserEntity user = userRepository.findById(session.userId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
    return user;
  }

  private AuthUserResponse toAuthUser(TcmsUserEntity user, List<String> perms) {
    return new AuthUserResponse(user.id, user.tenantId, user.fullName, user.email, user.roleName, perms);
  }
}

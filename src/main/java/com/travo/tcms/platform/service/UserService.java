package com.travo.tcms.platform.service;

import java.time.Instant;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import com.travo.tcms.platform.config.PasswordHasher;
import com.travo.tcms.platform.repository.TcmsUserRepository;
import com.travo.tcms.platform.entity.TcmsUserEntity;
import com.travo.tcms.platform.dto.UserRecord;
import com.travo.tcms.platform.dto.UserCreateRequest;
import com.travo.tcms.platform.dto.InternalUserRecord;
import com.travo.tcms.platform.dto.InternalUserRequest;

@Service
public class UserService {
  private final TcmsUserRepository userRepository;
  private final PasswordHasher passwords;

  public UserService(TcmsUserRepository userRepository, PasswordHasher passwords) {
    this.userRepository = userRepository;
    this.passwords = passwords;
  }

  public List<UserRecord> listUsers(Long tenantId) {
    return userRepository.findByUserTypeOrderByIdAsc("INTERNAL").stream()
        .filter(user -> tenantId == null || tenantId.equals(user.tenantId))
        .map(this::toUserRecord)
        .toList();
  }

  public UserRecord createUser(UserCreateRequest request) {
    TcmsUserEntity entity = new TcmsUserEntity();
    entity.externalId = "INT-" + String.format("%03d", userRepository.findByUserTypeOrderByIdAsc("INTERNAL").size() + 1);
    entity.tenantId = request.tenantId() == null ? 0L : request.tenantId();
    entity.userType = "INTERNAL";
    entity.fullName = request.fullName();
    entity.email = request.email();
    entity.password = passwords.hash(request.password());
    entity.roleName = request.role();
    entity.department = "Operations";
    entity.status = "Active";
    entity.lastLogin = "Never";
    entity.createdAt = Instant.now();
    userRepository.save(entity);
    return toUserRecord(entity);
  }

  public List<InternalUserRecord> listInternalUsers() {
    return userRepository.findByUserTypeOrderByIdAsc("INTERNAL").stream().map(this::toInternalUserRecord).toList();
  }

  public InternalUserRecord createInternalUser(InternalUserRequest request) {
    TcmsUserEntity entity = new TcmsUserEntity();
    entity.externalId = "INT-" + String.format("%03d", userRepository.findByUserTypeOrderByIdAsc("INTERNAL").size() + 1);
    entity.tenantId = 0L;
    entity.userType = "INTERNAL";
    entity.fullName = request.name();
    entity.email = request.email();
    entity.password = passwords.hash("Admin@123");
    entity.roleName = request.role();
    entity.department = request.department();
    entity.status = request.status();
    entity.lastLogin = "Never";
    entity.createdAt = Instant.now();
    userRepository.save(entity);
    return toInternalUserRecord(entity);
  }

  public InternalUserRecord updateInternalUser(String externalId, InternalUserRequest request) {
    TcmsUserEntity entity = userRepository.findByExternalId(externalId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    entity.fullName = request.name();
    entity.email = request.email();
    entity.roleName = request.role();
    entity.department = request.department();
    entity.status = request.status();
    userRepository.save(entity);
    return toInternalUserRecord(entity);
  }

  public void deleteInternalUser(String externalId) {
    TcmsUserEntity entity = userRepository.findByExternalId(externalId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    userRepository.delete(entity);
  }

  private UserRecord toUserRecord(TcmsUserEntity entity) {
    return new UserRecord(entity.id, entity.tenantId, entity.fullName, entity.email, entity.roleName,
        "Active".equalsIgnoreCase(entity.status), entity.createdAt);
  }

  private InternalUserRecord toInternalUserRecord(TcmsUserEntity entity) {
    return new InternalUserRecord(entity.externalId, entity.fullName, entity.roleName, entity.department,
        entity.email, entity.status, entity.lastLogin);
  }
}

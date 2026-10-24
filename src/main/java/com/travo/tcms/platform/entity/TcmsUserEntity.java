package com.travo.tcms.platform.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "tcms_users")
public class TcmsUserEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public Long id;
  public String externalId;
  public Long tenantId;
  public String userType;
  public String fullName;
  public String email;
  public String mobile;
  public String password;
  public String roleName;
  public String department;
  public String status;
  public String schoolName;
  public String subscription;
  public String licenseStatus;
  public String lastLogin;
  public Instant createdAt;
}

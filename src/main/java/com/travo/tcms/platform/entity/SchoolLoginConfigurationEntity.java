package com.travo.tcms.platform.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "tcms_school_login_configuration")
public class SchoolLoginConfigurationEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public Long id;
  public String adminUsername;
  public String passwordPolicy;
  public Boolean twoFactorAuthentication;
  public Integer passwordExpiryDays;
}

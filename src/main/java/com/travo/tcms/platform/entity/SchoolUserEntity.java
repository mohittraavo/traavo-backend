package com.travo.tcms.platform.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "school_users")
public class SchoolUserEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public Long id;

  public Long schoolId;
  public String schoolName;
  public String fullName;

  @Column(unique = true)
  public String email;

  public String password;
  public String role;
  public String status;
  public String lastLogin;
}

package com.travo.tcms.platform.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name = "tcms_roles")
public class RoleEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public Long id;
  public String externalId;
  public String roleName;
  public String description;
  public int usersAssigned;
  public int modulesAssigned;
  public String createdBy;
  public LocalDate createdDate;
  public String status;
  public String type;
}

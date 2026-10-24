package com.travo.tcms.platform.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "tcms_tickets")
public class TicketEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public Long id;
  public Long tenantId;
  public String campusName;
  public String subject;
  public String description;
  public String priority;
  public String status;
  public String resolutionNote;
  public Instant createdAt;
  public Instant updatedAt;
}

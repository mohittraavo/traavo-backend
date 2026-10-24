package com.travo.tcms.platform.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "tcms_school_communication")
public class SchoolCommunicationEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public Long id;
  public Boolean smsEnabled;
  public Boolean whatsappEnabled;
  public Boolean emailEnabled;
  public Boolean pushNotification;
  public String languagePreference;
}

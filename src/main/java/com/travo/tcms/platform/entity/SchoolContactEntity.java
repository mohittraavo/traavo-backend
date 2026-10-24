package com.travo.tcms.platform.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "tcms_school_contacts")
public class SchoolContactEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public Long id;
  public String schoolEmail;
  public String alternateEmail;
  public String mobileNumber;
  public String alternateMobile;
  public String landline;
  public String stdCode;
  public String website;
  public String facebookUrl;
  public String instagramUrl;
  public String linkedinUrl;
}

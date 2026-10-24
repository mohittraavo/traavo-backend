package com.travo.tcms.platform.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "tcms_school_addresses")
public class SchoolAddressEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public Long id;
  public String addressLine1;
  public String addressLine2;
  public String landmark;
  public String village;
  public String city;
  public String district;
  public String state;
  public String country;
  public String pinCode;
  public Double latitude;
  public Double longitude;
}

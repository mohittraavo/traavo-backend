package com.travo.tcms.platform.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "tcms_school_hostel")
public class SchoolHostelEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public Long id;
  public Boolean hostelAvailable;
  public Boolean boysHostel;
  public Boolean girlsHostel;
  public Integer numberOfRooms;
  public Integer capacity;

  @OneToMany(cascade = CascadeType.ALL)
  @JoinColumn(name = "hostel_id")
  public java.util.List<SchoolWardenEntity> wardens;
}

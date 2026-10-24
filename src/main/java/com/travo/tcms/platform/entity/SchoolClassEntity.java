package com.travo.tcms.platform.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "tcms_school_classes")
public class SchoolClassEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public Long id;
  public String className;
  public Integer maximumStrength;
  public Integer currentStrength;
  public Integer numberOfSections;
  public String medium;
  public String shift;

  @OneToMany(cascade = CascadeType.ALL)
  @JoinColumn(name = "class_id")
  public java.util.List<SchoolSectionEntity> sections;
}

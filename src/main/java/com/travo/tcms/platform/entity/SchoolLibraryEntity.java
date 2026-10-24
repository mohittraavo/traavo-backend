package com.travo.tcms.platform.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "tcms_school_library")
public class SchoolLibraryEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public Long id;
  public Integer totalBooks;
  public Boolean eLibrary;
  public Boolean rfidEnabled;
  public String librarySoftware;
}

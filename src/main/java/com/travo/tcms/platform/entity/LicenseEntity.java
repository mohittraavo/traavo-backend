package com.travo.tcms.platform.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "tcms_licenses")
public class LicenseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    public String code;

    public String schoolName;

    public String board;

    public String academicYear;

    public int campusCount;

    public int studentCapacity;

    public String contactName;

    public String contactEmail;

    public String status;

    public LocalDate validFrom;

    public LocalDate validTo;

    public Long activatedTenantId;

    public String licenseType;

    public Instant createdAt;
}
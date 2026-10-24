package com.travo.tcms.platform.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "tcms_schools")
public class SchoolEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public Long id;
  public String externalId;
  public String schoolName;
  public String schoolCode;
  public String schoolShortName;
  public String schoolType;
  public String boardsCsv;
  public String mediumsCsv;
  public String udiseCode;
  public LocalDate affiliationValidTill;
  public LocalDate affiliationStartDate;
  public String schoolStatus;
  public String registrationNumber;
  public String affiliationNumber;
  public String board;
  public String subscriptionPlan;
  public String licenseStatus;
  public int totalCampuses;
  public int totalStudents;
  public String schoolAdminName;
  public String schoolAdminEmail;
  public String schoolAdminPhone;
  public String schoolAdminLastLogin;
  public String status;
  public LocalDate createdDate;
  public int establishedYear;
  public String website;
  public String logo;
  public String email;
  public String phone;
  public LocalDate subscriptionStartDate;
  public LocalDate subscriptionExpiryDate;
  public int subscriptionGracePeriodDays;
  public LocalDate subscriptionGracePeriodEndDate;
  public String billingCycle;
  public String paymentStatus;
  public int studentLimit;
  public int campusLimit;
  public String featureFlagsCsv;
  public String licenseKey;
  public String licenseType;
  public LocalDate activeUntil;
  public LocalDate renewalDate;
  public String licenseHistoryCsv;
  public String themeColor;
  public String schoolDomain;
  public String emailBranding;
  public int teachers;
  public String revenue;
  public int activeUsers;
  public String permissionsCsv;
  public String auditLogCsv;

  // One-to-one detailed sub-entities
  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "management_id")
  public SchoolManagementEntity management;

  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "academic_session_id")
  public SchoolAcademicSessionEntity academicSession;

  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "academic_statistics_id")
  public SchoolAcademicStatisticsEntity academicStatistics;

  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "infrastructure_id")
  public SchoolInfrastructureEntity infrastructure;

  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "fees_id")
  public SchoolFeesEntity fees;

  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "erp_configuration_id")
  public SchoolErpConfigurationEntity erpConfiguration;

  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "login_configuration_id")
  public SchoolLoginConfigurationEntity loginConfiguration;

  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "communication_id")
  public SchoolCommunicationEntity communication;

  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "transport_id")
  public SchoolTransportEntity transport;

  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "hostel_id")
  public SchoolHostelEntity hostel;

  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "library_id")
  public SchoolLibraryEntity library;

  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "sports_id")
  public SchoolSportsEntity sports;

  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "medical_id")
  public SchoolMedicalEntity medical;

  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "compliance_id")
  public SchoolComplianceEntity compliance;

  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "integrations_id")
  public SchoolIntegrationsEntity integrations;

  // One-to-many lists
  @OneToMany(cascade = CascadeType.ALL)
  @JoinColumn(name = "school_id")
  public java.util.List<SchoolClassEntity> classes;

  @OneToMany(cascade = CascadeType.ALL)
  @JoinColumn(name = "school_id")
  public java.util.List<SchoolFeeHeadEntity> feeHeads;

  @OneToMany(cascade = CascadeType.ALL)
  @JoinColumn(name = "school_id")
  public java.util.List<SchoolBankAccountEntity> bankAccounts;

  @OneToMany(cascade = CascadeType.ALL)
  @JoinColumn(name = "school_id")
  public java.util.List<SchoolDocumentEntity> documents;

  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "address_id")
  public SchoolAddressEntity address;

  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "contact_id")
  public SchoolContactEntity contact;

  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "principal_id")
  public SchoolPrincipalEntity principal;
}


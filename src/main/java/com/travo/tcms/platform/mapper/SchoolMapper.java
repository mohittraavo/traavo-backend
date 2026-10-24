package com.travo.tcms.platform.mapper;

import com.travo.tcms.platform.dto.*;
import com.travo.tcms.platform.entity.SchoolEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public final class SchoolMapper {
  private SchoolMapper() {}

  public static SchoolDetailRecord toDto(SchoolEntity entity) {
    if (entity == null) return null;

    SubscriptionInfo subscription = new SubscriptionInfo(
        entity.subscriptionPlan,
        entity.subscriptionStartDate,
        entity.subscriptionExpiryDate,
        entity.billingCycle,
        entity.paymentStatus,
        entity.studentLimit,
        entity.campusLimit,
        parseCsvToList(entity.featureFlagsCsv));

    LicenseInfo license = new LicenseInfo(
        entity.licenseKey,
        entity.licenseType,
        entity.activeUntil,
        entity.renewalDate,
        parseLicenseHistory(entity.licenseHistoryCsv));

    SchoolAdminInfo schoolAdmin = new SchoolAdminInfo(
        // prefer principal sub-entity if available for richer info
        entity.principal != null ? entity.principal.principalName : entity.schoolAdminName,
        entity.principal != null ? entity.principal.email : entity.schoolAdminEmail,
        entity.principal != null ? entity.principal.mobile : entity.schoolAdminPhone,
        entity.schoolAdminLastLogin);

    BrandingInfo branding = new BrandingInfo(entity.logo, entity.themeColor, entity.schoolDomain, entity.emailBranding);

    AnalyticsInfo analytics = new AnalyticsInfo(entity.totalStudents, entity.teachers, entity.totalCampuses, entity.revenue, entity.activeUsers);

    List<SchoolPermissionGroup> permissions = parsePermissions(entity.permissionsCsv);
    List<String> auditLog = parseAuditLog(entity.auditLogCsv);

    // helper for null-safe integers
    
    String registrationNumber = entity.registrationNumber != null ? entity.registrationNumber
        : (entity.management != null ? entity.management.registrationNumber : null);

    String website = entity.website != null ? entity.website
        : (entity.contact != null ? entity.contact.website : null);

    String email = entity.email != null ? entity.email
        : (entity.contact != null ? entity.contact.schoolEmail : null);

    String phone = entity.phone != null ? entity.phone
        : (entity.contact != null ? entity.contact.mobileNumber : null);

    // Prefer explicit totals, otherwise derive from academic statistics
    int students = entity.totalStudents > 0 ? entity.totalStudents
        : (entity.academicStatistics != null ?
            (safeInt(entity.academicStatistics.boys) + safeInt(entity.academicStatistics.girls)) : 0);
    int teachers = entity.teachers > 0 ? entity.teachers
        : (entity.academicStatistics != null ? safeInt(entity.academicStatistics.teachers) : 0);

    AnalyticsInfo enrichedAnalytics = new AnalyticsInfo(students, teachers, entity.totalCampuses, entity.revenue, entity.activeUsers);

    AddressDto addressDto = entity.address == null ? null : new AddressDto(
        entity.address.addressLine1,
        entity.address.addressLine2,
        entity.address.landmark,
        entity.address.village,
        entity.address.city,
        entity.address.district,
        entity.address.state,
        entity.address.country,
        entity.address.pinCode,
        entity.address.latitude,
        entity.address.longitude
    );

    ContactDto contactDto = entity.contact == null ? null : new ContactDto(
        entity.contact.schoolEmail,
        entity.contact.alternateEmail,
        entity.contact.mobileNumber,
        entity.contact.alternateMobile,
        entity.contact.landline,
        entity.contact.stdCode,
        entity.contact.website,
        entity.contact.facebookUrl,
        entity.contact.instagramUrl,
        entity.contact.linkedinUrl
    );

    ManagementDto managementDto = entity.management == null ? null : new ManagementDto(
        entity.management.trustName,
        entity.management.societyName,
        entity.management.chairmanName,
        entity.management.directorName,
        entity.management.secretaryName,
        entity.management.managementEmail,
        entity.management.managementMobile,
        entity.management.registrationNumber,
        entity.management.registrationDate,
        entity.management.gstNumber,
        entity.management.panNumber
    );

    AcademicSessionDto sessionDto = entity.academicSession == null ? null : new AcademicSessionDto(
        entity.academicSession.academicSession,
        entity.academicSession.startDate,
        entity.academicSession.endDate
    );

    AcademicStatisticsDto statsDto = entity.academicStatistics == null ? null : new AcademicStatisticsDto(
        entity.academicStatistics.boys,
        entity.academicStatistics.girls,
        entity.academicStatistics.teachers,
        entity.academicStatistics.teachingStaff,
        entity.academicStatistics.nonTeachingStaff
    );

    List<ClassDto> classes = entity.classes == null ? java.util.List.of() : entity.classes.stream().map(c -> {
      java.util.List<SectionDto> secs = c.sections == null ? java.util.List.of() : c.sections.stream()
          .map(s -> new SectionDto(s.sectionName)).toList();
      return new ClassDto(c.className, c.maximumStrength, c.currentStrength, c.numberOfSections, c.medium, c.shift, secs);
    }).toList();

    InfrastructureDto infraDto = entity.infrastructure == null ? null : new InfrastructureDto(
        entity.infrastructure.campusArea,
        entity.infrastructure.buildingArea,
        entity.infrastructure.smartClasses,
        entity.infrastructure.computerLabs,
        entity.infrastructure.scienceLabs,
        entity.infrastructure.libraryPresent,
        entity.infrastructure.playground,
        entity.infrastructure.auditorium,
        entity.infrastructure.hostel,
        entity.infrastructure.transport,
        entity.infrastructure.cctvInstalled,
        entity.infrastructure.biometricInstalled,
        entity.infrastructure.fireSafetyCertificate,
        entity.infrastructure.drinkingWater,
        entity.infrastructure.toiletsBoys,
        entity.infrastructure.toiletsGirls,
        entity.infrastructure.staffRooms,
        entity.infrastructure.medicalRoom
    );

    FeesDto feesDto = entity.fees == null ? null : new FeesDto(
        entity.fees.admissionFee,
        entity.fees.tuitionFee,
        entity.fees.annualFee,
        entity.fees.examFee,
        entity.fees.transportFee,
        entity.fees.hostelFee,
        entity.fees.feeFrequency
    );

    List<FeeHeadDto> feeHeads = entity.feeHeads == null ? java.util.List.of() : entity.feeHeads.stream()
        .map(fh -> new FeeHeadDto(fh.headName, fh.amount, null)).toList();

    List<BankAccountDto> bankAccounts = entity.bankAccounts == null ? java.util.List.of() : entity.bankAccounts.stream()
        .map(b -> new BankAccountDto(b.bankName, b.branch, b.ifsc, b.accountNumber, b.accountHolderName, null))
        .toList();

    List<DocumentDto> documents = entity.documents == null ? java.util.List.of() : entity.documents.stream()
        .map(d -> new DocumentDto(d.name, d.url)).toList();

    ErpConfigurationDto erpDto = entity.erpConfiguration == null ? null : new ErpConfigurationDto(
        entity.erpConfiguration.erpEnabled,
        entity.erpConfiguration.lmsEnabled,
        entity.erpConfiguration.attendanceModule,
        entity.erpConfiguration.transportModule,
        entity.erpConfiguration.hostelModule,
        entity.erpConfiguration.libraryModule,
        entity.erpConfiguration.hrModule,
        entity.erpConfiguration.payrollModule,
        entity.erpConfiguration.financeModule,
        entity.erpConfiguration.parentApp,
        entity.erpConfiguration.studentApp,
        entity.erpConfiguration.teacherApp
    );

    LoginConfigurationDto loginDto = entity.loginConfiguration == null ? null : new LoginConfigurationDto(
        entity.loginConfiguration.adminUsername,
        entity.loginConfiguration.passwordPolicy,
        entity.loginConfiguration.twoFactorAuthentication,
        entity.loginConfiguration.passwordExpiryDays
    );

    CommunicationDto commDto = entity.communication == null ? null : new CommunicationDto(
        entity.communication.smsEnabled,
        entity.communication.whatsappEnabled,
        entity.communication.emailEnabled,
        entity.communication.pushNotification,
        entity.communication.languagePreference
    );

    TransportDto transportDto = null;
    if (entity.transport != null) {
      java.util.List<DriverDto> drivers = entity.transport.drivers == null ? java.util.List.of() : entity.transport.drivers.stream()
          .map(d -> new DriverDto(d.name, d.phone, d.licenseNumber)).toList();
      java.util.List<RouteDto> routes = entity.transport.routes == null ? java.util.List.of() : entity.transport.routes.stream().map(r -> {
        java.util.List<String> stops = parseDelimitedList(r.stopsCsv, "|");
        return new RouteDto(r.routeName, null, null, stops);
      }).toList();
      java.util.List<VehicleDto> vehicles = entity.transport.vehicles == null ? java.util.List.of() : entity.transport.vehicles.stream()
          .map(v -> new VehicleDto(v.vehicleNumber, v.model, v.capacity)).toList();
      transportDto = new TransportDto(entity.transport.transportAvailable, entity.transport.numberOfBuses, entity.transport.gpsEnabled, drivers, routes, vehicles);
    }

    HostelDto hostelDto = entity.hostel == null ? null : new HostelDto(
        entity.hostel.hostelAvailable,
        entity.hostel.boysHostel,
        entity.hostel.girlsHostel,
        entity.hostel.numberOfRooms,
        entity.hostel.capacity,
        entity.hostel.wardens == null ? java.util.List.of() : entity.hostel.wardens.stream().map(w -> new WardenDto(w.name, w.contact)).toList()
    );

    LibraryDto libraryDto = entity.library == null ? null : new LibraryDto(entity.library.totalBooks, entity.library.eLibrary, entity.library.rfidEnabled, entity.library.librarySoftware);

    SportsDto sportsDto = entity.sports == null ? null : new SportsDto(
        entity.sports.indoorGamesCsv == null ? java.util.List.of() : java.util.Arrays.stream(entity.sports.indoorGamesCsv.split("\\|"))
            .map(String::trim).filter(s -> !s.isEmpty()).toList(),
        entity.sports.outdoorGamesCsv == null ? java.util.List.of() : java.util.Arrays.stream(entity.sports.outdoorGamesCsv.split("\\|"))
            .map(String::trim).filter(s -> !s.isEmpty()).toList(),
        entity.sports.swimmingPool,
        entity.sports.gymnasium,
        entity.sports.coachAvailable
    );

    MedicalDto medicalDto = entity.medical == null ? null : new MedicalDto(entity.medical.medicalRoom, entity.medical.doctorAvailable, entity.medical.nurseAvailable, entity.medical.ambulance, entity.medical.nearestHospital);

    ComplianceDto complianceDto = entity.compliance == null ? null : new ComplianceDto(entity.compliance.childSafetyPolicy, entity.compliance.poshPolicy, entity.compliance.antiRaggingPolicy, entity.compliance.disasterManagementPlan, entity.compliance.insuranceDetails);

    IntegrationsDto integrationsDto = entity.integrations == null ? null : new IntegrationsDto(entity.integrations.paymentGateway, entity.integrations.smsProvider, entity.integrations.whatsappApi, entity.integrations.googleWorkspace, entity.integrations.microsoft365, entity.integrations.zoomIntegration, entity.integrations.biometricVendor);

    return new SchoolDetailRecord(
        entity.externalId,
        entity.schoolName,
        entity.schoolCode,
        registrationNumber,
        entity.affiliationNumber,
        entity.board,
        entity.establishedYear,
        website,
        entity.logo,
        email,
        phone,
        subscription,
        license,
        schoolAdmin,
        branding,
        enrichedAnalytics,
        addressDto,
        contactDto,
        managementDto,
        sessionDto,
        statsDto,
        classes,
        infraDto,
        feesDto,
        feeHeads,
        bankAccounts,
        documents,
        erpDto,
        loginDto,
        commDto,
        transportDto,
        hostelDto,
        libraryDto,
        sportsDto,
        medicalDto,
        complianceDto,
        integrationsDto,
        permissions,
        auditLog);
  }

  private static int safeInt(Integer v) {
    return v == null ? 0 : v;
  }

  private static List<String> parseCsvToList(String csv) {
    return parseDelimitedList(csv, ",");
  }

  private static List<String> parseDelimitedList(String value, String delimiter) {
    if (value == null || value.isBlank()) return Collections.emptyList();
    return Arrays.stream(value.split(java.util.regex.Pattern.quote(delimiter)))
        .map(String::trim)
        .filter(s -> !s.isEmpty())
        .collect(Collectors.toList());
  }

  private static List<String> parseLicenseHistory(String csvValue) {
    return parseDelimitedList(csvValue, "|");
  }

  private static List<SchoolPermissionGroup> parsePermissions(String csvValue) {
    if (csvValue == null || csvValue.isBlank()) return Collections.emptyList();
    List<SchoolPermissionGroup> out = new ArrayList<>();
    String[] parts = csvValue.split(";\\s*");
    for (String part : parts) {
      String[] mp = part.split(":", 2);
      if (mp.length != 2) continue;
      String module = mp[0].trim();
      List<RolePermissionItem> perms = Arrays.stream(mp[1].split(","))
          .map(String::trim)
          .filter(s -> !s.isEmpty())
          .map(p -> {
            String[] pp = p.split("\\|", 2);
            String name = pp[0].trim();
            boolean enabled = pp.length > 1 && (pp[1].trim().equalsIgnoreCase("true") || pp[1].trim().equals("1"));
            return new RolePermissionItem(name, enabled);
          }).collect(Collectors.toList());
      out.add(new SchoolPermissionGroup(module, perms));
    }
    return out;
  }

  private static List<String> parseAuditLog(String csvValue) {
    return parseDelimitedList(csvValue, "|");
  }
}

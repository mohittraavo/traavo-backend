package com.travo.tcms.platform.dto;

import java.time.LocalDate;
import java.util.List;

public record SchoolDetailRecord(String id, String schoolName, String schoolCode, String registrationNumber,
                          String affiliationNumber, String board, int establishedYear, String website, String logo,
                          String email, String phone, SubscriptionInfo subscription, LicenseInfo license,
                          SchoolAdminInfo schoolAdmin, BrandingInfo branding, AnalyticsInfo analytics,
                          AddressDto address, ContactDto contact, ManagementDto management,
                          AcademicSessionDto academicSession, AcademicStatisticsDto academicStatistics,
                          List<ClassDto> classes, InfrastructureDto infrastructure, FeesDto fees,
                          List<FeeHeadDto> feeHeads, List<BankAccountDto> bankAccounts, List<DocumentDto> documents,
                          ErpConfigurationDto erpConfiguration, LoginConfigurationDto loginConfiguration,
                          CommunicationDto communication, TransportDto transport, HostelDto hostel,
                          LibraryDto library, SportsDto sports, MedicalDto medical, ComplianceDto compliance,
                          IntegrationsDto integrations, List<SchoolPermissionGroup> permissions, List<String> auditLog) {
}

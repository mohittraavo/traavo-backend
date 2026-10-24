package com.travo.tcms.platform.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record SchoolOnboardingRequest(
    @NotNull @Valid SchoolDto school,
    @NotNull @Valid AddressDto address,
    @NotNull @Valid ContactDto contact,
    @NotNull @Valid PrincipalDto principal,
    @Valid ManagementDto management,
    @NotNull @Valid AcademicSessionDto academicSession,
    @NotNull @Valid AcademicStatisticsDto academicStatistics,
    @Valid List<ClassDto> classes,
    @Valid InfrastructureDto infrastructure,
    @Valid FeesDto fees,
    List<FeeHeadDto> feeHeads,
    @Valid List<BankAccountDto> bankAccounts,
    List<DocumentDto> documents,
    @Valid ErpConfigurationDto erpConfiguration,
    @Valid LoginConfigurationDto loginConfiguration,
    @Valid CommunicationDto communication,
    @Valid TransportDto transport,
    @Valid HostelDto hostel,
    @Valid LibraryDto library,
    @Valid SportsDto sports,
    @Valid MedicalDto medical,
    @Valid ComplianceDto compliance,
    @Valid IntegrationsDto integrations
) {}

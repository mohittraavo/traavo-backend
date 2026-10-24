package com.travo.tcms.platform.dto;

public record ErpConfigurationDto(
    Boolean erpEnabled,
    Boolean lmsEnabled,
    Boolean attendanceModule,
    Boolean transportModule,
    Boolean hostelModule,
    Boolean libraryModule,
    Boolean hrModule,
    Boolean payrollModule,
    Boolean financeModule,
    Boolean parentApp,
    Boolean studentApp,
    Boolean teacherApp
) {}

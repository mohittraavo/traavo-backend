package com.travo.tcms.platform.dto;

import jakarta.validation.constraints.Email;
import java.time.LocalDate;

public record ManagementDto(
    String trustName,
    String societyName,
    String chairmanName,
    String directorName,
    String secretaryName,
    @Email String managementEmail,
    String managementMobile,
    String registrationNumber,
    LocalDate registrationDate,
    String gstNumber,
    String panNumber
) {}

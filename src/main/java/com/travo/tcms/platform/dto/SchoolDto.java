package com.travo.tcms.platform.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.List;

public record SchoolDto(
    @NotBlank String schoolName,
    @NotBlank String schoolCode,
    String schoolShortName,
    @NotBlank String schoolType,
    @NotEmpty List<String> boards,
    @NotBlank String schoolCategory,
    @NotEmpty List<String> mediums,
    @NotNull @Min(1800) Integer establishmentYear,
    @NotBlank String udiseCode,
    String affiliationNumber,
    LocalDate affiliationValidTill,
    String schoolStatus,
    String schoolLogo,
    LocalDate affiliationStartDate
) {}

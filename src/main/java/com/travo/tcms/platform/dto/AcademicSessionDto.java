package com.travo.tcms.platform.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record AcademicSessionDto(
    @NotBlank String academicSession,
    @NotNull LocalDate startDate,
    @NotNull LocalDate endDate
) {}

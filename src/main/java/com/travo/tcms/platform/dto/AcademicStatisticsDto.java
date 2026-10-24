package com.travo.tcms.platform.dto;

import jakarta.validation.constraints.Min;

public record AcademicStatisticsDto(
    @Min(0) Integer boys,
    @Min(0) Integer girls,
    @Min(0) Integer teachers,
    @Min(0) Integer teachingStaff,
    @Min(0) Integer nonTeachingStaff
) {}

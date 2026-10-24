package com.travo.tcms.platform.dto;

import java.util.List;

public record ClassDto(
    String className,
    Integer maximumStrength,
    Integer currentStrength,
    Integer numberOfSections,
    String medium,
    String shift,
    List<SectionDto> sections
) {}

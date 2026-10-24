package com.travo.tcms.platform.dto;

public record MedicalDto(
    Boolean medicalRoom,
    Boolean doctorAvailable,
    Boolean nurseAvailable,
    Boolean ambulance,
    String nearestHospital
) {}

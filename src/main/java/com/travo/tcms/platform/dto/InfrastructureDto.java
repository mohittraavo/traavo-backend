package com.travo.tcms.platform.dto;

public record InfrastructureDto(
    Double campusArea,
    Double buildingArea,
    Integer smartClasses,
    Integer computerLabs,
    Integer scienceLabs,
    Boolean library,
    Boolean playground,
    Boolean auditorium,
    Boolean hostel,
    Boolean transport,
    Boolean cctvInstalled,
    Boolean biometricInstalled,
    String fireSafetyCertificate,
    Boolean drinkingWater,
    Integer toiletsBoys,
    Integer toiletsGirls,
    Integer staffRooms,
    Boolean medicalRoom
) {}

package com.travo.tcms.platform.dto;

public record SchoolAuthUserResponse(
    Long userId,
    Long schoolId,
    String fullName,
    String email,
    String role,
    String school
) {
}

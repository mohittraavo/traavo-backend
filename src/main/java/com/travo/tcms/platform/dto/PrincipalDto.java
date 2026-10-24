package com.travo.tcms.platform.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

public record PrincipalDto(
    @NotBlank String principalName,
    String gender,
    LocalDate dob,
    String mobile,
    @Email String email,
    String qualification,
    Integer experience,
    LocalDate joiningDate,
    String aadhaarNumber,
    String panNumber,
    String photo
) {}

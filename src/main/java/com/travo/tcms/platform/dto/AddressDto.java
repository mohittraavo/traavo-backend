package com.travo.tcms.platform.dto;

import jakarta.validation.constraints.*;

public record AddressDto(
    @NotBlank String addressLine1,
    String addressLine2,
    String landmark,
    String village,
    @NotBlank String city,
    @NotBlank String district,
    @NotBlank String state,
    @NotBlank String country,
    @NotBlank @Pattern(regexp = "\\d{6}") String pinCode,
    Double latitude,
    Double longitude
) {}

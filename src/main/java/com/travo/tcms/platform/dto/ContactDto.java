package com.travo.tcms.platform.dto;

import jakarta.validation.constraints.Email;

public record ContactDto(
    @Email String schoolEmail,
    @Email String alternateEmail,
    String mobileNumber,
    String alternateMobile,
    String landline,
    String stdCode,
    String website,
    String facebookUrl,
    String instagramUrl,
    String linkedinUrl
) {}

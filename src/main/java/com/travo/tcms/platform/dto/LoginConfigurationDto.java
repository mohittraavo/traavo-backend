package com.travo.tcms.platform.dto;

public record LoginConfigurationDto(
    String adminUsername,
    String passwordPolicy,
    Boolean twoFactorAuthentication,
    Integer passwordExpiryDays
) {}

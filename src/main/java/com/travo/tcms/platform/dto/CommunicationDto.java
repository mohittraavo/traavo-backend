package com.travo.tcms.platform.dto;

public record CommunicationDto(
    Boolean smsEnabled,
    Boolean whatsappEnabled,
    Boolean emailEnabled,
    Boolean pushNotification,
    String languagePreference
) {}

package com.travo.tcms.platform.dto;

public record IntegrationsDto(
    String paymentGateway,
    String smsProvider,
    String whatsappApi,
    Boolean googleWorkspace,
    Boolean microsoft365,
    Boolean zoomIntegration,
    String biometricVendor
) {}

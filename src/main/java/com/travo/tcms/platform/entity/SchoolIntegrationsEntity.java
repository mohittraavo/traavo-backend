package com.travo.tcms.platform.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "tcms_school_integrations")
public class SchoolIntegrationsEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public Long id;
  public String paymentGateway;
  public String smsProvider;
  public String whatsappApi;
  public Boolean googleWorkspace;
  public Boolean microsoft365;
  public Boolean zoomIntegration;
  public String biometricVendor;
}

package com.travo.tcms.platform.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "tcms_school_fees")
public class SchoolFeesEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public Long id;
  public BigDecimal admissionFee;
  public BigDecimal tuitionFee;
  public BigDecimal annualFee;
  public BigDecimal examFee;
  public BigDecimal transportFee;
  public BigDecimal hostelFee;
  public String feeFrequency;
}

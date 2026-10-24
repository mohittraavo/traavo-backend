package com.travo.tcms.platform.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "tcms_school_fee_heads")
public class SchoolFeeHeadEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public Long id;
  public String headName;
  public BigDecimal amount;
}

package com.travo.tcms.platform.model;

import java.math.BigDecimal;

public class FeeLine {
  public String id;
  public String feeHead;
  public BigDecimal amount;
  public String frequency;
  public String dueDate;
  public boolean discountEligible;
  public boolean lateFine;
  public String type;

  public FeeLine() {
  }
}

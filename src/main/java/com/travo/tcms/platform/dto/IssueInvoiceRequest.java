package com.travo.tcms.platform.dto;

import java.math.BigDecimal;

public class IssueInvoiceRequest {
  public Long studentId;
  public String feePeriod;
  public BigDecimal baseAmount;
  public BigDecimal concession;
  public BigDecimal lateFine;
  public String dueDate;
}

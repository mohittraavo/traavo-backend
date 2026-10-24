package com.travo.tcms.platform.dto;

import java.math.BigDecimal;

public class RecordPaymentRequest {
  public BigDecimal amount;
  public String receiptNo;
  public String paymentDate;
  public String paymentMethod;
  public String referenceNo;
  public String remarks;
}

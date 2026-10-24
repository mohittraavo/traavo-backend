package com.travo.tcms.platform.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ClassFeeSchedule {
  public String className;
  public String sectionsScope;
  public List<FeeLine> components = new ArrayList<>();
  public BigDecimal concession;
  public BigDecimal lateFineAmount;
  public String status;

  public ClassFeeSchedule() {
  }
}

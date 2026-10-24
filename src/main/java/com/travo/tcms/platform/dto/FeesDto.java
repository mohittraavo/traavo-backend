package com.travo.tcms.platform.dto;

import java.math.BigDecimal;

public record FeesDto(
    BigDecimal admissionFee,
    BigDecimal tuitionFee,
    BigDecimal annualFee,
    BigDecimal examFee,
    BigDecimal transportFee,
    BigDecimal hostelFee,
    String feeFrequency
) {}

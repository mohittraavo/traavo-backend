package com.travo.tcms.platform.dto;

import java.math.BigDecimal;

public record FeeHeadDto(
    String name,
    BigDecimal amount,
    String frequency
) {}

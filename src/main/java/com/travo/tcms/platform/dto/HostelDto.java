package com.travo.tcms.platform.dto;

import java.util.List;

public record HostelDto(
    Boolean hostelAvailable,
    Boolean boysHostel,
    Boolean girlsHostel,
    Integer numberOfRooms,
    Integer capacity,
    List<WardenDto> wardens
) {}

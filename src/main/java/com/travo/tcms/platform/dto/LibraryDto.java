package com.travo.tcms.platform.dto;

public record LibraryDto(
    Integer totalBooks,
    Boolean eLibrary,
    Boolean rfidEnabled,
    String librarySoftware
) {}

package com.travo.tcms.platform.dto;

public record InternalUserRecord(String id, String name, String role, String department, String email, String status,
                                 String lastLogin) {
}

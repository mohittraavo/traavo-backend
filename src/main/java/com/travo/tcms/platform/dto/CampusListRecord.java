package com.travo.tcms.platform.dto;

public record CampusListRecord(String id, String campusName, String campusCode, String schoolName, String city,
                        String principal, int students, int teachers, int classes, String status) {
}

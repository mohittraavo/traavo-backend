package com.travo.tcms.platform.dto;

public record CampusCapacityRecord(String id, String campusName, String schoolName, int maxStudents, int currentStudents,
                            int maxTeachers, int currentTeachers) {
}

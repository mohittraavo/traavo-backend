package com.travo.tcms.platform.dto;

public record CampusCapacityInfo(int maxStudents, int currentStudents, int maxTeachers, int currentTeachers,
                           int buildings, int classrooms) {
}

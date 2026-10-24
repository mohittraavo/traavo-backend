package com.travo.tcms.platform.dto;

import java.util.List;

public record CampusDetailRecord(String id, String schoolName, String campusName, String campusCode, String city,
                          String status, CampusBasicInfo basic, CampusPrincipalInfo principal,
                          CampusCapacityInfo capacity, CampusAcademicInfo academic, List<String> facilities,
                          CampusStatisticsInfo statistics, CampusTimingInfo timings,
                          List<SchoolPermissionGroup> permissions) {
}

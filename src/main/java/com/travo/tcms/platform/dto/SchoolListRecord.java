package com.travo.tcms.platform.dto;

import java.time.LocalDate;

public record SchoolListRecord(String id, String schoolName, String schoolCode, String board, String subscriptionPlan,
                                String licenseStatus, int totalCampuses, int totalStudents, String schoolAdmin, String status,
                                LocalDate createdDate) {
}

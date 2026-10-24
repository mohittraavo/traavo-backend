package com.travo.tcms.platform.dto;

import com.travo.tcms.platform.entity.TeachingAssignmentEntity;
import java.util.ArrayList;
import java.util.List;

public class TeachingRosterRow {
  public Long staffId;
  public String name;
  public String employeeCode;
  public String department;
  public String designation;
  public String campus;
  public int weeklyCapacity;
  public int assignedPeriods;
  public int capacityLeft;
  public String conflict;
  public boolean classTeacher;
  public String classesLabel;
  public String sectionsLabel;
  public String subjectsLabel;
  public List<TeachingAssignmentEntity> assignments = new ArrayList<>();
}

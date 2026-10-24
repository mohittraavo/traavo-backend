package com.travo.tcms.platform.util;

import com.travo.tcms.platform.entity.StaffMemberEntity;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class StaffCsv {
  private StaffCsv() {}

  public static List<Map<String, String>> parse(String text) {
    List<List<String>> rows = splitRows(text);
    if (rows.isEmpty()) return List.of();
    List<String> headers = rows.get(0).stream().map(StaffCsv::headerKey).toList();
    List<Map<String, String>> records = new ArrayList<>();
    for (int i = 1; i < rows.size(); i++) {
      List<String> cells = rows.get(i);
      if (cells.stream().allMatch(String::isBlank)) continue;
      Map<String, String> record = new LinkedHashMap<>();
      record.put("_row", String.valueOf(i + 1));
      for (int c = 0; c < headers.size(); c++) {
        String key = headers.get(c);
        if (key.isEmpty()) continue;
        record.put(key, c < cells.size() ? cells.get(c).trim() : "");
      }
      records.add(record);
    }
    return records;
  }

  public static StaffMemberEntity toEntity(Map<String, String> row) {
    StaffMemberEntity staff = new StaffMemberEntity();
    staff.employeeCode = value(row, "employeecode", "employee code", "code");
    staff.firstName = value(row, "firstname", "first name");
    staff.lastName = value(row, "lastname", "last name");
    staff.department = value(row, "department");
    staff.designation = value(row, "designation");
    staff.category = value(row, "category");
    staff.phone = value(row, "phone", "mobile", "mobilephone");
    staff.email = value(row, "email", "officialemail");
    staff.campus = value(row, "campus");
    staff.joiningDate = value(row, "joiningdate", "joining date");
    staff.employmentType = value(row, "employmenttype", "employment type", "tenure");
    return staff;
  }

  public static String headerKey(String header) {
    return header == null ? "" : header.toLowerCase(Locale.ROOT).replace("*", "").replaceAll("[^a-z0-9]", "");
  }

  private static String value(Map<String, String> row, String... keys) {
    for (String key : keys) {
      String found = row.get(headerKey(key));
      if (found != null && !found.isBlank()) return found.trim();
    }
    return "";
  }

  private static List<List<String>> splitRows(String text) {
    List<List<String>> rows = new ArrayList<>();
    List<String> current = new ArrayList<>();
    StringBuilder cell = new StringBuilder();
    boolean quoted = false;
    String source = text == null ? "" : text.replace("\uFEFF", "");
    for (int i = 0; i < source.length(); i++) {
      char ch = source.charAt(i);
      if (quoted) {
        if (ch == '"') {
          if (i + 1 < source.length() && source.charAt(i + 1) == '"') {
            cell.append('"');
            i++;
          } else {
            quoted = false;
          }
        } else {
          cell.append(ch);
        }
        continue;
      }
      if (ch == '"') {
        quoted = true;
      } else if (ch == ',') {
        current.add(cell.toString());
        cell.setLength(0);
      } else if (ch == '\n') {
        current.add(cell.toString());
        cell.setLength(0);
        rows.add(current);
        current = new ArrayList<>();
      } else if (ch != '\r') {
        cell.append(ch);
      }
    }
    if (cell.length() > 0 || !current.isEmpty()) {
      current.add(cell.toString());
      rows.add(current);
    }
    return rows;
  }
}

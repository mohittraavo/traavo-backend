package com.travo.tcms.platform.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.travo.tcms.platform.entity.StudentEntity;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class StudentExcelHelper {
  private static final ObjectMapper MAPPER = new ObjectMapper();

  public static final List<String> TEMPLATE_HEADERS = Arrays.asList(
      "Admission No*",
      "Admission Date (YYYY-MM-DD)",
      "First Name*",
      "Last Name*",
      "Class / Grade*",
      "Section*",
      "Roll No",
      "Academic Session",
      "Date of Birth* (YYYY-MM-DD)",
      "Gender* (Male/Female/Other)",
      "Blood Group",
      "Mother Tongue",
      "Medium of Instruction",
      "APAAR ID (12-digit)",
      "UDISE PEN (11-digit)",
      "Student Aadhaar (12-digit)",
      "Aadhaar Status (Verified/Pending/Exempted)",
      "UDISE Student Code",
      "Birth Certificate No",
      "Birth Certificate Authority",
      "Domicile State",
      "Domicile Certificate No",
      "Admission Quota",
      "Category (General/OBC/SC/ST/EWS)",
      "Religion",
      "Minority Status (Yes/No)",
      "BPL Card No",
      "Family Annual Income",
      "CWSN Special Needs (Yes/No)",
      "Disability Type",
      "Disability Percentage",
      "UDID No",
      "Special Aids Provided",
      "Father Name*",
      "Father Mobile* (10-digit)",
      "Father Email",
      "Father Qualification",
      "Father Occupation",
      "Father Designation",
      "Father Organization",
      "Father Annual Income",
      "Father Aadhaar (12-digit)",
      "Father PAN",
      "Mother Name",
      "Mother Mobile (10-digit)",
      "Mother Email",
      "Mother Qualification",
      "Mother Occupation",
      "Mother Designation",
      "Mother Organization",
      "Mother Annual Income",
      "Mother Aadhaar (12-digit)",
      "Mother PAN",
      "Guardian Name",
      "Guardian Relation",
      "Guardian Phone",
      "Guardian Email",
      "Guardian Occupation",
      "Emergency Doctor Name",
      "Emergency Doctor Phone",
      "Preferred Hospital",
      "Residential Address",
      "Permanent Address",
      "City",
      "State",
      "Pincode",
      "Distance From School (KM)",
      "Previous School Name",
      "Previous Board",
      "Previous Class Passed",
      "TC Number",
      "TC Date",
      "TC Counter-Signed",
      "Transport Required",
      "Transport Mode",
      "Bus Route No",
      "Bus Stop / Pickup Point",
      "Morning Pickup Time",
      "Afternoon Drop Time",
      "Bank Account Holder Name",
      "Bank Name",
      "Bank Account Number",
      "Bank IFSC Code",
      "Bank Branch",
      "Scholarship Availed",
      "Height (cm)",
      "Weight (kg)",
      "BMI",
      "Vision Left",
      "Vision Right",
      "Allergies",
      "Chronic Conditions",
      "Vaccination Status"
  );

  private static final Map<String, BiConsumer<StudentEntity, String>> SETTERS = buildSetters();
  private static final Map<String, Function<StudentEntity, String>> GETTERS = buildGetters();

  public static List<StudentEntity> parseExcel(MultipartFile file) throws IOException {
    String filename = file.getOriginalFilename() == null ? "" : file.getOriginalFilename().toLowerCase(Locale.ROOT);
    if (filename.endsWith(".csv") || filename.endsWith(".txt") || isPlainText(file)) {
      return parseDelimited(new String(file.getBytes(), java.nio.charset.StandardCharsets.UTF_8));
    }
    try {
      return parseWorkbook(file);
    } catch (Exception excelError) {
      String text = new String(file.getBytes(), java.nio.charset.StandardCharsets.UTF_8);
      if (looksLikeCsv(text)) {
        return parseDelimited(text);
      }
      if (excelError instanceof IOException io) {
        throw io;
      }
      throw new IOException("Unable to read student file", excelError);
    }
  }

  private static boolean isPlainText(MultipartFile file) {
    String contentType = file.getContentType() == null ? "" : file.getContentType().toLowerCase(Locale.ROOT);
    return contentType.contains("csv") || contentType.contains("text/plain");
  }

  private static boolean looksLikeCsv(String text) {
    if (text == null) {
      return false;
    }
    String first = text.lines().map(String::trim).filter(line -> !line.isBlank()).findFirst().orElse("");
    return countCommas(first) >= 5;
  }

  private static int countCommas(String value) {
    int count = 0;
    for (int i = 0; i < value.length(); i++) {
      if (value.charAt(i) == ',') {
        count++;
      }
    }
    return count;
  }

  private static List<StudentEntity> parseWorkbook(MultipartFile file) throws IOException {
    List<StudentEntity> result = new ArrayList<>();
    try (Workbook wb = WorkbookFactory.create(file.getInputStream())) {
      Sheet sheet = wb.getSheetAt(0);
      Iterator<Row> rows = sheet.rowIterator();
      Row header = nextNonEmptyRow(rows);
      if (header == null) {
        return result;
      }
      Map<Integer, String> idxToHeader = readHeaders(header);
      if (idxToHeader.size() <= 1) {
        String only = idxToHeader.getOrDefault(0, cellValue(header.getCell(0)));
        if (looksLikeCsv(only)) {
          return parseSheetAsCsv(sheet);
        }
      }

      while (rows.hasNext()) {
        Row r = rows.next();
        List<String> values = rowValues(r, idxToHeader.size());
        StudentEntity student = studentFromValues(idxToHeader, values, r.getRowNum() + 1);
        if (student != null) {
          result.add(student);
        }
      }
    }
    return result;
  }

  private static List<StudentEntity> parseSheetAsCsv(Sheet sheet) {
    StringBuilder csv = new StringBuilder();
    for (Row row : sheet) {
      if (row == null) {
        continue;
      }
      String line = cellValue(row.getCell(0, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL));
      if (!line.isBlank()) {
        csv.append(line).append('\n');
      }
    }
    return parseDelimited(csv.toString());
  }

  private static List<StudentEntity> parseDelimited(String text) {
    List<StudentEntity> result = new ArrayList<>();
    List<String> lines = text.lines().map(StudentExcelHelper::stripBom).map(String::trim)
        .filter(line -> !line.isBlank())
        .toList();
    if (lines.isEmpty()) {
      return result;
    }
    List<String> headerCells = parseCsvLine(lines.get(0));
    Map<Integer, String> idxToHeader = new HashMap<>();
    for (int i = 0; i < headerCells.size(); i++) {
      idxToHeader.put(i, headerCells.get(i));
    }
    for (int i = 1; i < lines.size(); i++) {
      List<String> values = parseCsvLine(lines.get(i));
      StudentEntity student = studentFromValues(idxToHeader, values, i + 1);
      if (student != null) {
        result.add(student);
      }
    }
    return result;
  }

  private static StudentEntity studentFromValues(Map<Integer, String> idxToHeader, List<String> values, int rowNumber) {
    if (values.isEmpty()) {
      return null;
    }
    if (looksLikeCsv(values.get(0)) && blankAfterFirst(values)) {
      values = parseCsvLine(values.get(0));
    }
    if (!values.isEmpty() && normalize(values.get(0)).startsWith("admission no")) {
      return null;
    }
    StudentEntity student = new StudentEntity();
    Map<String, String> raw = new LinkedHashMap<>();
    boolean anyValue = false;
    int lastCol = Math.max(idxToHeader.size(), values.size());
    for (int ci = 0; ci < lastCol; ci++) {
      String headerName = idxToHeader.get(ci);
      if (headerName == null || headerName.isBlank()) {
        continue;
      }
      String val = ci < values.size() ? nullToEmpty(values.get(ci)).trim() : "";
      raw.put(headerName, val);
      if (!val.isBlank()) {
        anyValue = true;
      }
      applyHeader(student, headerName, val);
    }
    if (!anyValue) {
      return null;
    }
    raw.put("__row", String.valueOf(rowNumber));
    student.rawDataJson = toJson(raw);
    return student;
  }

  private static Map<Integer, String> readHeaders(Row header) {
    Map<Integer, String> idxToHeader = new HashMap<>();
    short last = header.getLastCellNum();
    for (int ci = 0; ci < last; ci++) {
      String name = cellValue(header.getCell(ci, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL));
      if (!name.isBlank()) {
        idxToHeader.put(ci, name);
      }
    }
    return idxToHeader;
  }

  private static List<String> rowValues(Row r, int headerCount) {
    List<String> values = new ArrayList<>();
    if (r == null) {
      return values;
    }
    int lastCol = Math.max(headerCount, Math.max(0, r.getLastCellNum()));
    for (int ci = 0; ci < lastCol; ci++) {
      values.add(cellValue(r.getCell(ci, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL)));
    }
    return values;
  }

  private static Row nextNonEmptyRow(Iterator<Row> rows) {
    while (rows.hasNext()) {
      Row row = rows.next();
      if (row == null) {
        continue;
      }
      for (Cell cell : row) {
        if (!cellValue(cell).isBlank()) {
          return row;
        }
      }
    }
    return null;
  }

  private static boolean blankAfterFirst(List<String> values) {
    for (int i = 1; i < values.size(); i++) {
      if (values.get(i) != null && !values.get(i).isBlank()) {
        return false;
      }
    }
    return true;
  }

  private static String stripBom(String line) {
    if (line != null && !line.isEmpty() && line.charAt(0) == '\uFEFF') {
      return line.substring(1);
    }
    return line;
  }

  static List<String> parseCsvLine(String line) {
    List<String> out = new ArrayList<>();
    if (line == null) {
      return out;
    }
    StringBuilder cur = new StringBuilder();
    boolean inQuotes = false;
    for (int i = 0; i < line.length(); i++) {
      char ch = line.charAt(i);
      if (inQuotes) {
        if (ch == '"') {
          if (i + 1 < line.length() && line.charAt(i + 1) == '"') {
            cur.append('"');
            i++;
          } else {
            inQuotes = false;
          }
        } else {
          cur.append(ch);
        }
      } else if (ch == '"') {
        inQuotes = true;
      } else if (ch == ',') {
        out.add(cur.toString().trim());
        cur.setLength(0);
      } else {
        cur.append(ch);
      }
    }
    out.add(cur.toString().trim());
    return out;
  }

  public static byte[] generateTemplate() throws IOException {
    try (Workbook wb = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
      CellStyle textStyle = wb.createCellStyle();
      textStyle.setDataFormat(wb.createDataFormat().getFormat("@"));

      Sheet students = wb.createSheet("students");
      Row header = students.createRow(0);
      for (int i = 0; i < TEMPLATE_HEADERS.size(); i++) {
        Cell cell = header.createCell(i);
        cell.setCellValue(TEMPLATE_HEADERS.get(i));
        cell.setCellStyle(textStyle);
        students.setDefaultColumnStyle(i, textStyle);
        students.setColumnWidth(i, 22 * 256);
      }

      Sheet instructions = wb.createSheet("instructions");
      String[][] notes = {
          {"Columns marked with * are required."},
          {"Dates must be YYYY-MM-DD (example: 2015-04-21)."},
          {"Gender must be Male, Female or Other."},
          {"Aadhaar Status must be Verified, Pending or Exempted."},
          {"Category must be General, OBC, SC, ST or EWS."},
          {"Minority Status and CWSN Special Needs must be Yes or No."},
          {"APAAR ID, Student Aadhaar, Father Aadhaar and Mother Aadhaar must be 12 digits."},
          {"UDISE PEN must be 11 digits. Father/Mother Mobile must be 10 digits."},
          {"Keep Aadhaar and ID columns as text so Excel does not convert them to numbers."}
      };
      for (int i = 0; i < notes.length; i++) {
        instructions.createRow(i).createCell(0).setCellValue(notes[i][0]);
      }
      instructions.setColumnWidth(0, 90 * 256);

      wb.write(out);
      return out.toByteArray();
    }
  }

  public static byte[] writeExcel(List<StudentEntity> students) throws IOException {
    try (Workbook wb = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
      Sheet sheet = wb.createSheet("students");
      Row header = sheet.createRow(0);
      for (int i = 0; i < TEMPLATE_HEADERS.size(); i++) {
        header.createCell(i).setCellValue(TEMPLATE_HEADERS.get(i));
      }
      int rowIndex = 1;
      for (StudentEntity student : students) {
        Row row = sheet.createRow(rowIndex++);
        for (int i = 0; i < TEMPLATE_HEADERS.size(); i++) {
          String headerName = TEMPLATE_HEADERS.get(i);
          Function<StudentEntity, String> getter = GETTERS.get(normalize(headerName));
          String value = getter == null ? "" : nullToEmpty(getter.apply(student));
          row.createCell(i).setCellValue(value);
        }
      }
      for (int i = 0; i < Math.min(TEMPLATE_HEADERS.size(), 20); i++) {
        sheet.autoSizeColumn(i);
      }
      wb.write(out);
      return out.toByteArray();
    }
  }

  public static class ValidationError {
    public int row;
    public String admissionNo;
    public List<String> messages = new ArrayList<>();
  }

  public static class ValidationResult {
    public List<StudentEntity> valid = new ArrayList<>();
    public List<ValidationError> errors = new ArrayList<>();
  }

  public static ValidationResult validateRows(List<StudentEntity> rows, Set<String> existingAdmissionNos) {
    ValidationResult res = new ValidationResult();
    Set<String> seenInFile = new HashSet<>();
    for (StudentEntity student : rows) {
      Map<String, String> raw = fromJson(student.rawDataJson);
      int rowNum = 0;
      try {
        rowNum = Integer.parseInt(raw.getOrDefault("__row", "0"));
      } catch (Exception ignored) {
        // keep 0
      }
      ValidationError error = new ValidationError();
      error.row = rowNum;
      error.admissionNo = student.admissionNo;

      require(error, student.admissionNo, "Admission No is required");
      require(error, student.firstName, "First Name is required");
      require(error, student.lastName, "Last Name is required");
      require(error, student.classGrade, "Class / Grade is required");
      require(error, student.section, "Section is required");
      require(error, student.dob, "Date of Birth is required");
      require(error, student.gender, "Gender is required");
      require(error, student.fatherName, "Father Name is required");
      require(error, student.fatherMobile, "Father Mobile is required");

      date(error, student.admissionDate, "Admission Date must be YYYY-MM-DD");
      date(error, student.dob, "Date of Birth must be YYYY-MM-DD");
      date(error, student.tcDate, "TC Date must be YYYY-MM-DD");

      enumValue(error, student.gender, List.of("Male", "Female", "Other"), "Gender must be Male, Female or Other");
      enumValue(error, student.aadhaarStatus, List.of("Verified", "Pending", "Exempted"),
          "Aadhaar Status must be Verified, Pending or Exempted");
      enumValue(error, student.category, List.of("General", "OBC", "SC", "ST", "EWS"),
          "Category must be General, OBC, SC, ST or EWS");
      enumValue(error, student.minorityStatus, List.of("Yes", "No"), "Minority Status must be Yes or No");
      enumValue(error, student.cwsnSpecialNeeds, List.of("Yes", "No"), "CWSN Special Needs must be Yes or No");

      digits(error, student.studentAadhaar, 12, "Student Aadhaar must be 12 digits");
      digits(error, student.fatherAadhaar, 12, "Father Aadhaar must be 12 digits");
      digits(error, student.motherAadhaar, 12, "Mother Aadhaar must be 12 digits");
      digits(error, student.apaarId, 12, "APAAR ID must be 12 digits");
      digits(error, student.udisePen, 11, "UDISE PEN must be 11 digits");
      digits(error, student.fatherMobile, 10, "Father Mobile must be 10 digits");
      digits(error, student.motherMobile, 10, "Mother Mobile must be 10 digits");
      digits(error, student.guardianPhone, 10, "Guardian Phone must be 10 digits");

      if (student.fatherPAN != null && !student.fatherPAN.isBlank()
          && !student.fatherPAN.matches("(?i)[A-Z]{5}[0-9]{4}[A-Z]")) {
        error.messages.add("Father PAN must be 10 characters (AAAAA9999A)");
      }
      if (student.motherPAN != null && !student.motherPAN.isBlank()
          && !student.motherPAN.matches("(?i)[A-Z]{5}[0-9]{4}[A-Z]")) {
        error.messages.add("Mother PAN must be 10 characters (AAAAA9999A)");
      }

      String admissionKey = student.admissionNo == null ? "" : student.admissionNo.trim().toLowerCase(Locale.ROOT);
      if (!admissionKey.isBlank()) {
        if (!seenInFile.add(admissionKey)) {
          error.messages.add("Duplicate Admission No in this file");
        }
        if (existingAdmissionNos != null && existingAdmissionNos.contains(admissionKey)) {
          error.messages.add("Admission No already exists");
        }
      }

      if (error.messages.isEmpty()) {
        res.valid.add(student);
      } else {
        res.errors.add(error);
      }
    }
    return res;
  }

  private static void require(ValidationError error, String value, String message) {
    if (value == null || value.isBlank()) {
      error.messages.add(message);
    }
  }

  private static void date(ValidationError error, String value, String message) {
    if (value != null && !value.isBlank() && !value.matches("\\d{4}-\\d{2}-\\d{2}")) {
      error.messages.add(message);
    }
  }

  private static void digits(ValidationError error, String value, int length, String message) {
    if (value != null && !value.isBlank() && !value.matches("\\d{" + length + "}")) {
      error.messages.add(message);
    }
  }

  private static void enumValue(ValidationError error, String value, List<String> allowed, String message) {
    if (value == null || value.isBlank()) {
      return;
    }
    for (String option : allowed) {
      if (option.equalsIgnoreCase(value.trim())) {
        return;
      }
    }
    error.messages.add(message);
  }

  private static void applyHeader(StudentEntity student, String headerName, String val) {
    BiConsumer<StudentEntity, String> setter = SETTERS.get(normalize(headerName));
    if (setter != null) {
      setter.accept(student, val == null ? "" : val.trim());
    }
  }

  private static String normalize(String header) {
    if (header == null) {
      return "";
    }
    String value = header.toLowerCase(Locale.ROOT).trim();
    value = value.replace("*", "");
    value = value.replaceAll("\\([^)]*\\)", " ");
    value = value.replaceAll("[^a-z0-9]+", " ").trim();
    return value.replaceAll("\\s+", " ");
  }

  private static String cellValue(Cell cell) {
    if (cell == null) {
      return "";
    }
    return switch (cell.getCellType()) {
      case STRING -> cell.getStringCellValue() == null ? "" : cell.getStringCellValue().trim();
      case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
      case NUMERIC -> {
        if (DateUtil.isCellDateFormatted(cell)) {
          yield cell.getLocalDateTimeCellValue().toLocalDate().toString();
        }
        yield BigDecimal.valueOf(cell.getNumericCellValue()).stripTrailingZeros().toPlainString();
      }
      case FORMULA -> {
        try {
          yield cell.getStringCellValue();
        } catch (Exception ignored) {
          yield BigDecimal.valueOf(cell.getNumericCellValue()).stripTrailingZeros().toPlainString();
        }
      }
      default -> "";
    };
  }

  private static String nullToEmpty(String value) {
    return value == null ? "" : value;
  }

  private static String toJson(Map<String, String> map) {
    try {
      return MAPPER.writeValueAsString(map);
    } catch (Exception e) {
      return "{}";
    }
  }

  private static Map<String, String> fromJson(String json) {
    if (json == null || json.isBlank()) {
      return Collections.emptyMap();
    }
    try {
      return MAPPER.readValue(json, new TypeReference<Map<String, String>>() { });
    } catch (Exception e) {
      return Collections.emptyMap();
    }
  }

  private static Map<String, BiConsumer<StudentEntity, String>> buildSetters() {
    Map<String, BiConsumer<StudentEntity, String>> map = new HashMap<>();
    map.put("admission no", (s, v) -> s.admissionNo = v);
    map.put("admission date", (s, v) -> s.admissionDate = v);
    map.put("first name", (s, v) -> s.firstName = v);
    map.put("last name", (s, v) -> s.lastName = v);
    map.put("class grade", (s, v) -> s.classGrade = v);
    map.put("class", (s, v) -> s.classGrade = v);
    map.put("grade", (s, v) -> s.classGrade = v);
    map.put("section", (s, v) -> s.section = v);
    map.put("roll no", (s, v) -> s.rollNo = v);
    map.put("academic session", (s, v) -> s.academicSession = v);
    map.put("date of birth", (s, v) -> s.dob = v);
    map.put("gender", (s, v) -> s.gender = v);
    map.put("blood group", (s, v) -> s.bloodGroup = v);
    map.put("mother tongue", (s, v) -> s.motherTongue = v);
    map.put("medium of instruction", (s, v) -> s.mediumOfInstruction = v);
    map.put("apaar id", (s, v) -> s.apaarId = v);
    map.put("udise pen", (s, v) -> s.udisePen = v);
    map.put("student aadhaar", (s, v) -> s.studentAadhaar = v);
    map.put("aadhaar status", (s, v) -> s.aadhaarStatus = v);
    map.put("udise student code", (s, v) -> s.udiseStudentCode = v);
    map.put("birth certificate no", (s, v) -> s.birthCertificateNo = v);
    map.put("birth certificate authority", (s, v) -> s.birthCertificateAuthority = v);
    map.put("domicile state", (s, v) -> s.domicileState = v);
    map.put("domicile certificate no", (s, v) -> s.domicileCertificateNo = v);
    map.put("admission quota", (s, v) -> s.admissionQuota = v);
    map.put("category", (s, v) -> s.category = v);
    map.put("religion", (s, v) -> s.religion = v);
    map.put("minority status", (s, v) -> s.minorityStatus = v);
    map.put("bpl card no", (s, v) -> s.bplCardNo = v);
    map.put("family annual income", (s, v) -> s.familyAnnualIncome = v);
    map.put("cwsn special needs", (s, v) -> s.cwsnSpecialNeeds = v);
    map.put("disability type", (s, v) -> s.disabilityType = v);
    map.put("disability percentage", (s, v) -> s.disabilityPercentage = v);
    map.put("udid no", (s, v) -> s.udidNo = v);
    map.put("special aids provided", (s, v) -> s.specialAidsProvided = v);
    map.put("father name", (s, v) -> s.fatherName = v);
    map.put("father mobile", (s, v) -> s.fatherMobile = v);
    map.put("father email", (s, v) -> s.fatherEmail = v);
    map.put("father qualification", (s, v) -> s.fatherQualification = v);
    map.put("father occupation", (s, v) -> s.fatherOccupation = v);
    map.put("father designation", (s, v) -> s.fatherDesignation = v);
    map.put("father organization", (s, v) -> s.fatherOrganization = v);
    map.put("father annual income", (s, v) -> s.fatherAnnualIncome = v);
    map.put("father aadhaar", (s, v) -> s.fatherAadhaar = v);
    map.put("father pan", (s, v) -> s.fatherPAN = v);
    map.put("mother name", (s, v) -> s.motherName = v);
    map.put("mother mobile", (s, v) -> s.motherMobile = v);
    map.put("mother email", (s, v) -> s.motherEmail = v);
    map.put("mother qualification", (s, v) -> s.motherQualification = v);
    map.put("mother occupation", (s, v) -> s.motherOccupation = v);
    map.put("mother designation", (s, v) -> s.motherDesignation = v);
    map.put("mother organization", (s, v) -> s.motherOrganization = v);
    map.put("mother annual income", (s, v) -> s.motherAnnualIncome = v);
    map.put("mother aadhaar", (s, v) -> s.motherAadhaar = v);
    map.put("mother pan", (s, v) -> s.motherPAN = v);
    map.put("guardian name", (s, v) -> s.guardianName = v);
    map.put("guardian relation", (s, v) -> s.guardianRelation = v);
    map.put("guardian phone", (s, v) -> s.guardianPhone = v);
    map.put("guardian email", (s, v) -> s.guardianEmail = v);
    map.put("guardian occupation", (s, v) -> s.guardianOccupation = v);
    map.put("emergency doctor name", (s, v) -> s.emergencyDoctorName = v);
    map.put("emergency doctor phone", (s, v) -> s.emergencyDoctorPhone = v);
    map.put("preferred hospital", (s, v) -> s.preferredHospital = v);
    map.put("residential address", (s, v) -> s.residentialAddress = v);
    map.put("permanent address", (s, v) -> s.permanentAddress = v);
    map.put("city", (s, v) -> s.city = v);
    map.put("state", (s, v) -> s.state = v);
    map.put("pincode", (s, v) -> s.pincode = v);
    map.put("distance from school km", (s, v) -> s.distanceFromSchoolKm = v);
    map.put("previous school name", (s, v) -> s.previousSchoolName = v);
    map.put("previous board", (s, v) -> s.previousBoard = v);
    map.put("previous class passed", (s, v) -> s.previousClassPassed = v);
    map.put("tc number", (s, v) -> s.tcNumber = v);
    map.put("tc date", (s, v) -> s.tcDate = v);
    map.put("tc counter signed", (s, v) -> s.tcCounterSigned = v);
    map.put("transport required", (s, v) -> s.transportRequired = v);
    map.put("transport mode", (s, v) -> s.transportMode = v);
    map.put("bus route no", (s, v) -> s.busRouteNo = v);
    map.put("bus stop pickup point", (s, v) -> s.busStopPickupPoint = v);
    map.put("morning pickup time", (s, v) -> s.morningPickupTime = v);
    map.put("afternoon drop time", (s, v) -> s.afternoonDropTime = v);
    map.put("bank account holder name", (s, v) -> s.bankAccountHolderName = v);
    map.put("bank name", (s, v) -> s.bankName = v);
    map.put("bank account number", (s, v) -> s.bankAccountNumber = v);
    map.put("bank ifsc code", (s, v) -> s.bankIFSC = v);
    map.put("bank ifsc", (s, v) -> s.bankIFSC = v);
    map.put("bank branch", (s, v) -> s.bankBranch = v);
    map.put("scholarship availed", (s, v) -> s.scholarshipAvailed = v);
    map.put("height cm", (s, v) -> s.heightCm = v);
    map.put("weight kg", (s, v) -> s.weightKg = v);
    map.put("bmi", (s, v) -> s.bmi = v);
    map.put("vision left", (s, v) -> s.visionLeft = v);
    map.put("vision right", (s, v) -> s.visionRight = v);
    map.put("allergies", (s, v) -> s.allergies = v);
    map.put("chronic conditions", (s, v) -> s.chronicConditions = v);
    map.put("vaccination status", (s, v) -> s.vaccinationStatus = v);
    return map;
  }

  private static Map<String, Function<StudentEntity, String>> buildGetters() {
    Map<String, Function<StudentEntity, String>> map = new HashMap<>();
    map.put("admission no", s -> s.admissionNo);
    map.put("admission date", s -> s.admissionDate);
    map.put("first name", s -> s.firstName);
    map.put("last name", s -> s.lastName);
    map.put("class grade", s -> s.classGrade);
    map.put("section", s -> s.section);
    map.put("roll no", s -> s.rollNo);
    map.put("academic session", s -> s.academicSession);
    map.put("date of birth", s -> s.dob);
    map.put("gender", s -> s.gender);
    map.put("blood group", s -> s.bloodGroup);
    map.put("mother tongue", s -> s.motherTongue);
    map.put("medium of instruction", s -> s.mediumOfInstruction);
    map.put("apaar id", s -> s.apaarId);
    map.put("udise pen", s -> s.udisePen);
    map.put("student aadhaar", s -> s.studentAadhaar);
    map.put("aadhaar status", s -> s.aadhaarStatus);
    map.put("udise student code", s -> s.udiseStudentCode);
    map.put("birth certificate no", s -> s.birthCertificateNo);
    map.put("birth certificate authority", s -> s.birthCertificateAuthority);
    map.put("domicile state", s -> s.domicileState);
    map.put("domicile certificate no", s -> s.domicileCertificateNo);
    map.put("admission quota", s -> s.admissionQuota);
    map.put("category", s -> s.category);
    map.put("religion", s -> s.religion);
    map.put("minority status", s -> s.minorityStatus);
    map.put("bpl card no", s -> s.bplCardNo);
    map.put("family annual income", s -> s.familyAnnualIncome);
    map.put("cwsn special needs", s -> s.cwsnSpecialNeeds);
    map.put("disability type", s -> s.disabilityType);
    map.put("disability percentage", s -> s.disabilityPercentage);
    map.put("udid no", s -> s.udidNo);
    map.put("special aids provided", s -> s.specialAidsProvided);
    map.put("father name", s -> s.fatherName);
    map.put("father mobile", s -> s.fatherMobile);
    map.put("father email", s -> s.fatherEmail);
    map.put("father qualification", s -> s.fatherQualification);
    map.put("father occupation", s -> s.fatherOccupation);
    map.put("father designation", s -> s.fatherDesignation);
    map.put("father organization", s -> s.fatherOrganization);
    map.put("father annual income", s -> s.fatherAnnualIncome);
    map.put("father aadhaar", s -> s.fatherAadhaar);
    map.put("father pan", s -> s.fatherPAN);
    map.put("mother name", s -> s.motherName);
    map.put("mother mobile", s -> s.motherMobile);
    map.put("mother email", s -> s.motherEmail);
    map.put("mother qualification", s -> s.motherQualification);
    map.put("mother occupation", s -> s.motherOccupation);
    map.put("mother designation", s -> s.motherDesignation);
    map.put("mother organization", s -> s.motherOrganization);
    map.put("mother annual income", s -> s.motherAnnualIncome);
    map.put("mother aadhaar", s -> s.motherAadhaar);
    map.put("mother pan", s -> s.motherPAN);
    map.put("guardian name", s -> s.guardianName);
    map.put("guardian relation", s -> s.guardianRelation);
    map.put("guardian phone", s -> s.guardianPhone);
    map.put("guardian email", s -> s.guardianEmail);
    map.put("guardian occupation", s -> s.guardianOccupation);
    map.put("emergency doctor name", s -> s.emergencyDoctorName);
    map.put("emergency doctor phone", s -> s.emergencyDoctorPhone);
    map.put("preferred hospital", s -> s.preferredHospital);
    map.put("residential address", s -> s.residentialAddress);
    map.put("permanent address", s -> s.permanentAddress);
    map.put("city", s -> s.city);
    map.put("state", s -> s.state);
    map.put("pincode", s -> s.pincode);
    map.put("distance from school km", s -> s.distanceFromSchoolKm);
    map.put("previous school name", s -> s.previousSchoolName);
    map.put("previous board", s -> s.previousBoard);
    map.put("previous class passed", s -> s.previousClassPassed);
    map.put("tc number", s -> s.tcNumber);
    map.put("tc date", s -> s.tcDate);
    map.put("tc counter signed", s -> s.tcCounterSigned);
    map.put("transport required", s -> s.transportRequired);
    map.put("transport mode", s -> s.transportMode);
    map.put("bus route no", s -> s.busRouteNo);
    map.put("bus stop pickup point", s -> s.busStopPickupPoint);
    map.put("morning pickup time", s -> s.morningPickupTime);
    map.put("afternoon drop time", s -> s.afternoonDropTime);
    map.put("bank account holder name", s -> s.bankAccountHolderName);
    map.put("bank name", s -> s.bankName);
    map.put("bank account number", s -> s.bankAccountNumber);
    map.put("bank ifsc code", s -> s.bankIFSC);
    map.put("bank ifsc", s -> s.bankIFSC);
    map.put("bank branch", s -> s.bankBranch);
    map.put("scholarship availed", s -> s.scholarshipAvailed);
    map.put("height cm", s -> s.heightCm);
    map.put("weight kg", s -> s.weightKg);
    map.put("bmi", s -> s.bmi);
    map.put("vision left", s -> s.visionLeft);
    map.put("vision right", s -> s.visionRight);
    map.put("allergies", s -> s.allergies);
    map.put("chronic conditions", s -> s.chronicConditions);
    map.put("vaccination status", s -> s.vaccinationStatus);
    return map;
  }
}

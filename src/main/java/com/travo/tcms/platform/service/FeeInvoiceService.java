package com.travo.tcms.platform.service;

import com.travo.tcms.platform.dto.FeeInvoicePreview;
import com.travo.tcms.platform.dto.IssueInvoiceRequest;
import com.travo.tcms.platform.dto.RecordPaymentRequest;
import com.travo.tcms.platform.entity.FeeInvoiceEntity;
import com.travo.tcms.platform.entity.FeeStructureEntity;
import com.travo.tcms.platform.entity.StudentEntity;
import com.travo.tcms.platform.model.ClassFeeSchedule;
import com.travo.tcms.platform.model.FeeLine;
import com.travo.tcms.platform.repository.FeeInvoiceRepository;
import com.travo.tcms.platform.repository.FeeStructureRepository;
import com.travo.tcms.platform.repository.StudentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
public class FeeInvoiceService {
  private static final List<String> PAYMENT_METHODS = List.of(
      "Cash",
      "UPI (Google Pay / PhonePe / Paytm)",
      "Card",
      "Net Banking",
      "Cheque",
      "Bank Transfer"
  );

  private static final List<String> PERIODS = List.of(
      "Annual",
      "Q1 (Apr - Jun)",
      "Q2 (Jul - Sep)",
      "Q3 (Oct - Dec)",
      "Q4 (Jan - Mar)"
  );

  private final FeeInvoiceRepository invoices;
  private final StudentRepository students;
  private final FeeStructureRepository structures;

  public FeeInvoiceService(FeeInvoiceRepository invoices, StudentRepository students, FeeStructureRepository structures) {
    this.invoices = invoices;
    this.students = students;
    this.structures = structures;
  }

  public List<FeeInvoiceEntity> list(String query, String status) {
    String needle = query == null ? "" : query.trim().toLowerCase(Locale.ROOT);
    String wanted = status == null ? "" : status.trim().toUpperCase(Locale.ROOT);
    return invoices.findAll().stream()
        .peek(this::refreshStatus)
        .filter(invoice -> wanted.isEmpty() || "ALL".equals(wanted) || wanted.equals(invoice.status))
        .filter(invoice -> needle.isEmpty()
            || contains(invoice.studentName, needle)
            || contains(invoice.invoiceNo, needle)
            || contains(invoice.admissionNo, needle))
        .sorted(Comparator.comparing((FeeInvoiceEntity invoice) -> invoice.createdDate == null ? "" : invoice.createdDate).reversed())
        .toList();
  }

  public FeeInvoicePreview preview(Long studentId) {
    StudentEntity student = student(studentId);
    Match match = matchStructure(student);
    FeeInvoicePreview preview = new FeeInvoicePreview();
    preview.studentId = student.id;
    preview.studentName = fullName(student);
    preview.classGrade = clean(student.classGrade);
    preview.gradeLabel = gradeLabel(student.classGrade);
    preview.admissionNo = clean(student.admissionNo);
    preview.enrollmentId = enrollmentId(student, match.structure());
    preview.feeStructureId = match.structure().id;
    preview.feeStructureName = match.structure().name;
    preview.effectiveDate = clean(match.structure().effectiveFrom);
    preview.baseAmount = baseOf(match.schedule());
    preview.concession = money(match.schedule().concession);
    preview.lateFine = money(match.schedule().lateFineAmount);
    preview.suggestedDueDate = suggestedDue(match);
    return preview;
  }

  public FeeInvoiceEntity issue(IssueInvoiceRequest request) {
    if (request == null || request.studentId == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Select a student.");
    }
    StudentEntity student = student(request.studentId);
    Match match = matchStructure(student);
    String period = clean(request.feePeriod);
    if (!PERIODS.contains(period)) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Choose a billing period.");
    }
    LocalDate due = parseDate(request.dueDate, "Payment due date");
    LocalDate effective = parseDate(match.structure().effectiveFrom, "Fee structure effective date");
    if (!due.isAfter(effective)) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          "Due date must be after the fee structure effective date (" + effective + "). A due is created only when the due date is later than the effective date.");
    }
    BigDecimal base = money(request.baseAmount);
    BigDecimal concession = money(request.concession);
    BigDecimal lateFine = money(request.lateFine);
    if (base.signum() <= 0) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Base amount must be greater than 0.");
    }
    if (concession.compareTo(base) > 0) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Concession cannot exceed the base amount.");
    }
    String periodLabel = periodLabel(period, due);
    if (invoices.existsByStudentIdAndFeePeriodAndStatusNot(student.id, periodLabel, "PAID")) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "This student already has an open invoice for " + periodLabel + ".");
    }

    FeeInvoiceEntity invoice = new FeeInvoiceEntity();
    invoice.id = UUID.randomUUID().toString();
    invoice.invoiceNo = nextNumber(due, period);
    invoice.studentId = student.id;
    invoice.studentName = fullName(student);
    invoice.classGrade = clean(student.classGrade);
    invoice.gradeLabel = gradeLabel(student.classGrade);
    invoice.admissionNo = clean(student.admissionNo);
    invoice.enrollmentId = enrollmentId(student, match.structure());
    invoice.feeStructureId = match.structure().id;
    invoice.feeStructureName = match.structure().name;
    invoice.effectiveDate = effective.toString();
    invoice.feePeriod = periodLabel;
    invoice.dueDate = due.toString();
    invoice.baseAmount = base;
    invoice.concession = concession;
    invoice.lateFine = lateFine;
    invoice.totalBill = base.subtract(concession).add(lateFine).setScale(2, RoundingMode.HALF_UP);
    invoice.amountPaid = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
    invoice.createdDate = OffsetDateTime.now().toString();
    refreshStatus(invoice);
    return invoices.save(invoice);
  }

  public FeeInvoiceEntity recordPayment(String id, RecordPaymentRequest request) {
    FeeInvoiceEntity invoice = invoices.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Invoice not found"));
    refreshStatus(invoice);
    if ("PAID".equals(invoice.status)) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "This invoice is already paid.");
    }
    if (request == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Settlement details are required.");
    }
    BigDecimal payment = money(request.amount);
    if (payment.signum() <= 0) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Settlement amount must be greater than 0.");
    }
    BigDecimal nextPaid = money(invoice.amountPaid).add(payment);
    if (nextPaid.compareTo(money(invoice.totalBill)) > 0) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Settlement cannot exceed the outstanding balance.");
    }
    LocalDate paidOn = parseDate(request.paymentDate, "Payment date");
    String method = clean(request.paymentMethod);
    if (!PAYMENT_METHODS.contains(method)) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Choose a payment method.");
    }
    invoice.amountPaid = nextPaid;
    invoice.lastSettlementAmount = payment;
    invoice.receiptNo = receiptNumber(request.receiptNo, paidOn);
    invoice.paymentDate = paidOn.toString();
    invoice.paymentMethod = method;
    invoice.referenceNo = clean(request.referenceNo);
    invoice.remarks = clean(request.remarks);
    refreshStatus(invoice);
    return invoices.save(invoice);
  }

  private void refreshStatus(FeeInvoiceEntity invoice) {
    BigDecimal total = money(invoice.totalBill);
    BigDecimal paid = money(invoice.amountPaid);
    if (paid.compareTo(total) >= 0 && total.signum() > 0) {
      invoice.status = "PAID";
    } else if (paid.signum() > 0) {
      invoice.status = "PARTIAL";
    } else if (LocalDate.now().isAfter(LocalDate.parse(invoice.dueDate))) {
      invoice.status = "OVERDUE";
    } else {
      invoice.status = "PENDING";
    }
  }

  private Match matchStructure(StudentEntity student) {
    List<FeeStructureEntity> active = structures.findAll().stream()
        .filter(structure -> "Active".equalsIgnoreCase(clean(structure.status)))
        .toList();
    Match found = null;
    for (FeeStructureEntity structure : active) {
      if (structure.classSchedules == null) continue;
      for (ClassFeeSchedule schedule : structure.classSchedules) {
        if (schedule == null || !covers(schedule.className, student.classGrade)) continue;
        if (found == null || clean(structure.effectiveFrom).compareTo(clean(found.structure().effectiveFrom)) > 0) {
          found = new Match(structure, schedule);
        }
      }
    }
    if (found == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          "No active fee structure covers " + gradeLabel(student.classGrade) + ". Publish a structure before issuing an invoice.");
    }
    if (clean(found.structure().effectiveFrom).isEmpty()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The fee structure needs an effective date before a due can be created.");
    }
    return found;
  }

  private String suggestedDue(Match match) {
    String effective = clean(match.structure().effectiveFrom);
    String latest = "";
    if (match.schedule().components == null) return "";
    for (FeeLine line : match.schedule().components) {
      String due = line == null ? "" : clean(line.dueDate);
      if (due.compareTo(effective) > 0 && due.compareTo(latest) > 0) latest = due;
    }
    return latest;
  }

  private BigDecimal baseOf(ClassFeeSchedule schedule) {
    if (schedule.components == null) return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
    return schedule.components.stream()
        .map(line -> line == null || line.amount == null ? BigDecimal.ZERO : line.amount)
        .reduce(BigDecimal.ZERO, BigDecimal::add)
        .setScale(2, RoundingMode.HALF_UP);
  }

  private String receiptNumber(String requested, LocalDate paidOn) {
    String candidate = clean(requested);
    if (candidate.matches("REC-\\d{4}-\\d{3,6}") && !invoices.existsByReceiptNo(candidate)) {
      return candidate;
    }
    String prefix = "REC-" + paidOn.getYear() + "-";
    long sequence = invoices.countByReceiptNoStartingWith(prefix) + 1;
    return prefix + String.format("%04d", sequence);
  }

  private String nextNumber(LocalDate due, String period) {
    String code = switch (period) {
      case "Q1 (Apr - Jun)" -> "Q1";
      case "Q2 (Jul - Sep)" -> "Q2";
      case "Q3 (Oct - Dec)" -> "Q3";
      case "Q4 (Jan - Mar)" -> "Q4";
      default -> "ANN";
    };
    String prefix = "INV-" + due.getYear() + "-" + code + "-";
    long sequence = invoices.countByInvoiceNoStartingWith(prefix) + 1;
    return prefix + String.format("%03d", sequence);
  }

  private String periodLabel(String period, LocalDate due) {
    String year = String.valueOf(due.getYear());
    if (period.endsWith(")")) {
      return period.substring(0, period.length() - 1) + " " + year + ")";
    }
    return period + " " + year;
  }

  private String enrollmentId(StudentEntity student, FeeStructureEntity structure) {
    String year = clean(structure.academicYear);
    if (year.length() >= 4) year = year.substring(0, 4);
    else year = String.valueOf(LocalDate.now().getYear());
    String admission = clean(student.admissionNo);
    if (admission.isEmpty()) admission = String.valueOf(student.id);
    return "ENR-" + year + "-" + admission;
  }

  private StudentEntity student(Long id) {
    return students.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found"));
  }

  private boolean covers(String scheduleClass, String studentClass) {
    String left = clean(scheduleClass).toLowerCase(Locale.ROOT).replaceFirst("^class\\s+", "");
    String right = clean(studentClass).toLowerCase(Locale.ROOT).replaceFirst("^class\\s+", "");
    return !left.isEmpty() && left.equals(right);
  }

  private String gradeLabel(String classGrade) {
    String value = clean(classGrade);
    if (value.isEmpty()) return "Grade";
    if (value.toLowerCase(Locale.ROOT).startsWith("grade")) return value;
    String number = value.toLowerCase(Locale.ROOT).replaceFirst("^class\\s+", "");
    if (number.matches("\\d+")) return "Grade " + number;
    return value;
  }

  private String fullName(StudentEntity student) {
    return (clean(student.firstName) + " " + clean(student.lastName)).trim();
  }

  private LocalDate parseDate(String value, String label) {
    try {
      return LocalDate.parse(clean(value));
    } catch (Exception exception) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, label + " must be a valid date.");
    }
  }

  private BigDecimal money(BigDecimal value) {
    if (value == null) return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
    if (value.signum() < 0) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Amounts cannot be negative.");
    }
    return value.setScale(2, RoundingMode.HALF_UP);
  }

  private boolean contains(String value, String needle) {
    return value != null && value.toLowerCase(Locale.ROOT).contains(needle);
  }

  private String clean(String value) {
    return value == null ? "" : value.trim();
  }

  private record Match(FeeStructureEntity structure, ClassFeeSchedule schedule) {
  }
}

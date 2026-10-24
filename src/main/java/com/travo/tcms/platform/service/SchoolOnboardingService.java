package com.travo.tcms.platform.service;

import com.travo.tcms.platform.dto.SchoolOnboardingRequest;
import com.travo.tcms.platform.dto.SchoolOnboardingResponse;
import com.travo.tcms.platform.entity.*;
import com.travo.tcms.platform.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.UUID;

@Service
public class SchoolOnboardingService {

    private final SchoolRepository schoolRepository;

    public SchoolOnboardingService(SchoolRepository schoolRepository) {
        this.schoolRepository = schoolRepository;
    }

    @Transactional
    public SchoolOnboardingResponse createSchool(SchoolOnboardingRequest request) {
        SchoolEntity entity = new SchoolEntity();
        entity.externalId = "SCH-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        entity.schoolName = request.school().schoolName();
        entity.schoolCode = request.school().schoolCode();
        entity.schoolShortName = request.school().schoolShortName();
        entity.schoolType = request.school().schoolType();
        // persist first-class values and CSV for multi-value fields
        entity.boardsCsv = request.school().boards() == null ? null : String.join("|", request.school().boards());
        entity.mediumsCsv = request.school().mediums() == null ? null : String.join("|", request.school().mediums());
        // set primary/first values for quick access
        if (request.school().boards() != null && !request.school().boards().isEmpty()) {
            entity.board = request.school().boards().get(0);
        }
        if (request.school().mediums() != null && !request.school().mediums().isEmpty()) {
            // mediumsCsv already stores the list; keep first medium as board summary if needed
            entity.board = entity.board == null ? request.school().mediums().get(0) : entity.board;
        }
        entity.establishedYear = request.school().establishmentYear() == null ? 0 : request.school().establishmentYear();
        entity.udiseCode = request.school().udiseCode();
        entity.affiliationNumber = request.school().affiliationNumber();
        entity.affiliationValidTill = request.school().affiliationValidTill();
        entity.affiliationStartDate = request.school().affiliationStartDate();
        entity.schoolStatus = request.school().schoolStatus();
        entity.logo = request.school().schoolLogo();
        entity.status = "CREATED";
        entity.createdDate = LocalDate.now();

        // address
        if (request.address() != null) {
            SchoolAddressEntity addr = new SchoolAddressEntity();
            addr.addressLine1 = request.address().addressLine1();
            addr.addressLine2 = request.address().addressLine2();
            addr.landmark = request.address().landmark();
            addr.village = request.address().village();
            addr.city = request.address().city();
            addr.district = request.address().district();
            addr.state = request.address().state();
            addr.country = request.address().country();
            addr.pinCode = request.address().pinCode();
            try {
                addr.latitude = request.address().latitude() == null ? null : Double.valueOf(request.address().latitude());
            } catch (NumberFormatException e) {
                addr.latitude = null;
            }
            try {
                addr.longitude = request.address().longitude() == null ? null : Double.valueOf(request.address().longitude());
            } catch (NumberFormatException e) {
                addr.longitude = null;
            }
            entity.address = addr;
        }

        // contact
        if (request.contact() != null) {
            SchoolContactEntity contact = new SchoolContactEntity();
            contact.schoolEmail = request.contact().schoolEmail();
            contact.alternateEmail = request.contact().alternateEmail();
            contact.mobileNumber = request.contact().mobileNumber();
            contact.alternateMobile = request.contact().alternateMobile();
            contact.landline = request.contact().landline();
            contact.stdCode = request.contact().stdCode();
            contact.website = request.contact().website();
            contact.facebookUrl = request.contact().facebookUrl();
            contact.instagramUrl = request.contact().instagramUrl();
            contact.linkedinUrl = request.contact().linkedinUrl();
            entity.contact = contact;
        }

        // principal
        if (request.principal() != null) {
            SchoolPrincipalEntity p = new SchoolPrincipalEntity();
            p.principalName = request.principal().principalName();
            p.gender = request.principal().gender();
            try {
                p.dob = request.principal().dob() == null ? null : LocalDate.parse(request.principal().dob().toString());
            } catch (DateTimeParseException ex) {
                p.dob = null;
            }
            p.mobile = request.principal().mobile();
            p.email = request.principal().email();
            p.qualification = request.principal().qualification();
            p.experience = request.principal().experience();
            try {
                p.joiningDate = request.principal().joiningDate() == null ? null : LocalDate.parse(request.principal().joiningDate().toString());
            } catch (DateTimeParseException ex) {
                p.joiningDate = null;
            }
            p.aadhaarNumber = request.principal().aadhaarNumber();
            p.panNumber = request.principal().panNumber();
            p.photo = request.principal().photo();
            entity.principal = p;
        }

        // management
        if (request.management() != null) {
            SchoolManagementEntity m = new SchoolManagementEntity();
            m.trustName = request.management().trustName();
            m.societyName = request.management().societyName();
            m.chairmanName = request.management().chairmanName();
            m.directorName = request.management().directorName();
            m.secretaryName = request.management().secretaryName();
            m.managementEmail = request.management().managementEmail();
            m.managementMobile = request.management().managementMobile();
            m.registrationNumber = request.management().registrationNumber();
            m.registrationDate = request.management().registrationDate();
            m.gstNumber = request.management().gstNumber();
            m.panNumber = request.management().panNumber();
            entity.management = m;
        }

        // academic session
        if (request.academicSession() != null) {
            SchoolAcademicSessionEntity s = new SchoolAcademicSessionEntity();
            s.academicSession = request.academicSession().academicSession();
            s.startDate = request.academicSession().startDate();
            s.endDate = request.academicSession().endDate();
            entity.academicSession = s;
        }

        // academic statistics
        if (request.academicStatistics() != null) {
            SchoolAcademicStatisticsEntity stats = new SchoolAcademicStatisticsEntity();
            stats.boys = request.academicStatistics().boys();
            stats.girls = request.academicStatistics().girls();
            stats.teachers = request.academicStatistics().teachers();
            stats.teachingStaff = request.academicStatistics().teachingStaff();
            stats.nonTeachingStaff = request.academicStatistics().nonTeachingStaff();
            entity.academicStatistics = stats;
        }

        // classes & sections
        if (request.classes() != null && !request.classes().isEmpty()) {
            entity.classes = new java.util.ArrayList<>();
            for (var c : request.classes()) {
                SchoolClassEntity ce = new SchoolClassEntity();
                ce.className = c.className();
                ce.maximumStrength = c.maximumStrength();
                ce.currentStrength = c.currentStrength();
                ce.numberOfSections = c.numberOfSections();
                ce.medium = c.medium();
                ce.shift = c.shift();
                if (c.sections() != null && !c.sections().isEmpty()) {
                    ce.sections = new java.util.ArrayList<>();
                    for (var s : c.sections()) {
                        SchoolSectionEntity se = new SchoolSectionEntity();
                        se.sectionName = s.sectionName();
                        ce.sections.add(se);
                    }
                }
                entity.classes.add(ce);
            }
        }

        // infrastructure
        if (request.infrastructure() != null) {
            SchoolInfrastructureEntity inf = new SchoolInfrastructureEntity();
            inf.campusArea = request.infrastructure().campusArea();
            inf.buildingArea = request.infrastructure().buildingArea();
            inf.smartClasses = request.infrastructure().smartClasses();
            inf.computerLabs = request.infrastructure().computerLabs();
            inf.scienceLabs = request.infrastructure().scienceLabs();
            inf.libraryPresent = request.infrastructure().library();
            inf.playground = request.infrastructure().playground();
            inf.auditorium = request.infrastructure().auditorium();
            inf.hostel = request.infrastructure().hostel();
            inf.transport = request.infrastructure().transport();
            inf.cctvInstalled = request.infrastructure().cctvInstalled();
            inf.biometricInstalled = request.infrastructure().biometricInstalled();
            inf.fireSafetyCertificate = request.infrastructure().fireSafetyCertificate();
            inf.drinkingWater = request.infrastructure().drinkingWater();
            inf.toiletsBoys = request.infrastructure().toiletsBoys();
            inf.toiletsGirls = request.infrastructure().toiletsGirls();
            inf.staffRooms = request.infrastructure().staffRooms();
            inf.medicalRoom = request.infrastructure().medicalRoom();
            entity.infrastructure = inf;
        }

        // fees & fee heads
        if (request.fees() != null) {
            SchoolFeesEntity fe = new SchoolFeesEntity();
            fe.admissionFee = request.fees().admissionFee();
            fe.tuitionFee = request.fees().tuitionFee();
            fe.annualFee = request.fees().annualFee();
            fe.examFee = request.fees().examFee();
            fe.transportFee = request.fees().transportFee();
            fe.hostelFee = request.fees().hostelFee();
            fe.feeFrequency = request.fees().feeFrequency();
            entity.fees = fe;
        }
        if (request.feeHeads() != null && !request.feeHeads().isEmpty()) {
            entity.feeHeads = new java.util.ArrayList<>();
            for (var fh : request.feeHeads()) {
                SchoolFeeHeadEntity fhe = new SchoolFeeHeadEntity();
                fhe.headName = fh.name();
                fhe.amount = fh.amount();
                entity.feeHeads.add(fhe);
            }
        }

        // bank accounts
        if (request.bankAccounts() != null && !request.bankAccounts().isEmpty()) {
            entity.bankAccounts = new java.util.ArrayList<>();
            for (var b : request.bankAccounts()) {
                SchoolBankAccountEntity be = new SchoolBankAccountEntity();
                be.bankName = b.bankName();
                be.accountNumber = b.accountNumber();
                be.ifsc = b.ifsc();
                be.branch = b.branch();
                be.accountHolderName = b.accountHolder();
                entity.bankAccounts.add(be);
            }
        }

        // documents
        if (request.documents() != null && !request.documents().isEmpty()) {
            entity.documents = new java.util.ArrayList<>();
            for (var d : request.documents()) {
                SchoolDocumentEntity de = new SchoolDocumentEntity();
                de.name = d.documentType();
                de.type = "file";
                de.url = d.file();
                entity.documents.add(de);
            }
        }

        // erpConfiguration
        if (request.erpConfiguration() != null) {
            SchoolErpConfigurationEntity erp = new SchoolErpConfigurationEntity();
            erp.erpEnabled = request.erpConfiguration().erpEnabled();
            erp.lmsEnabled = request.erpConfiguration().lmsEnabled();
            erp.attendanceModule = request.erpConfiguration().attendanceModule();
            erp.transportModule = request.erpConfiguration().transportModule();
            erp.hostelModule = request.erpConfiguration().hostelModule();
            erp.libraryModule = request.erpConfiguration().libraryModule();
            erp.hrModule = request.erpConfiguration().hrModule();
            erp.payrollModule = request.erpConfiguration().payrollModule();
            erp.financeModule = request.erpConfiguration().financeModule();
            erp.parentApp = request.erpConfiguration().parentApp();
            erp.studentApp = request.erpConfiguration().studentApp();
            erp.teacherApp = request.erpConfiguration().teacherApp();
            entity.erpConfiguration = erp;
        }

        // loginConfiguration
        if (request.loginConfiguration() != null) {
            SchoolLoginConfigurationEntity lc = new SchoolLoginConfigurationEntity();
            lc.adminUsername = request.loginConfiguration().adminUsername();
            lc.passwordPolicy = request.loginConfiguration().passwordPolicy();
            lc.twoFactorAuthentication = request.loginConfiguration().twoFactorAuthentication();
            lc.passwordExpiryDays = request.loginConfiguration().passwordExpiryDays();
            entity.loginConfiguration = lc;
        }

        // communication
        if (request.communication() != null) {
            SchoolCommunicationEntity comm = new SchoolCommunicationEntity();
            comm.smsEnabled = request.communication().smsEnabled();
            comm.whatsappEnabled = request.communication().whatsappEnabled();
            comm.emailEnabled = request.communication().emailEnabled();
            comm.pushNotification = request.communication().pushNotification();
            comm.languagePreference = request.communication().languagePreference();
            entity.communication = comm;
        }

        // transport
        if (request.transport() != null) {
            SchoolTransportEntity te = new SchoolTransportEntity();
            te.transportAvailable = request.transport().transportAvailable();
            te.numberOfBuses = request.transport().numberOfBuses();
            te.gpsEnabled = request.transport().gpsEnabled();
            // drivers
            if (request.transport().drivers() != null && !request.transport().drivers().isEmpty()) {
                te.drivers = new java.util.ArrayList<>();
                for (var dv : request.transport().drivers()) {
                    SchoolDriverEntity de = new SchoolDriverEntity();
                    de.name = dv.name();
                    de.licenseNumber = dv.licenseNumber();
                    de.phone = dv.mobile();
                    te.drivers.add(de);
                }
            }
            // routes
            if (request.transport().routes() != null && !request.transport().routes().isEmpty()) {
                te.routes = new java.util.ArrayList<>();
                for (var r : request.transport().routes()) {
                    SchoolRouteEntity re = new SchoolRouteEntity();
                    re.routeName = r.routeName();
                    // store stops as JSON string to avoid complex mapping
                    re.stopsCsv = r.stops() == null ? null : String.join("|", r.stops());
                    te.routes.add(re);
                }
            }
            // vehicles
            if (request.transport().vehicles() != null && !request.transport().vehicles().isEmpty()) {
                te.vehicles = new java.util.ArrayList<>();
                for (var v : request.transport().vehicles()) {
                    SchoolVehicleEntity ve = new SchoolVehicleEntity();
                    ve.vehicleNumber = v.registrationNumber();
                    ve.model = v.vehicleType();
                    ve.capacity = v.capacity();
                    te.vehicles.add(ve);
                }
            }
            entity.transport = te;
        }

        // hostel
        if (request.hostel() != null) {
            SchoolHostelEntity he = new SchoolHostelEntity();
            he.hostelAvailable = request.hostel().hostelAvailable();
            he.boysHostel = request.hostel().boysHostel();
            he.girlsHostel = request.hostel().girlsHostel();
            he.numberOfRooms = request.hostel().numberOfRooms();
            he.capacity = request.hostel().capacity();
            if (request.hostel().wardens() != null && !request.hostel().wardens().isEmpty()) {
                he.wardens = new java.util.ArrayList<>();
                for (var w : request.hostel().wardens()) {
                    SchoolWardenEntity we = new SchoolWardenEntity();
                    we.name = w.name();
                    we.contact = w.mobile();
                    he.wardens.add(we);
                }
            }
            entity.hostel = he;
        }

        // library
        if (request.library() != null) {
            SchoolLibraryEntity le = new SchoolLibraryEntity();
            le.totalBooks = request.library().totalBooks();
            le.eLibrary = request.library().eLibrary();
            le.rfidEnabled = request.library().rfidEnabled();
            le.librarySoftware = request.library().librarySoftware();
            entity.library = le;
        }

        // sports
        if (request.sports() != null) {
            SchoolSportsEntity se = new SchoolSportsEntity();
            se.indoorGamesCsv = request.sports().indoorGames() == null ? null : String.join("|", request.sports().indoorGames());
            se.outdoorGamesCsv = request.sports().outdoorGames() == null ? null : String.join("|", request.sports().outdoorGames());
            se.swimmingPool = request.sports().swimmingPool();
            se.gymnasium = request.sports().gymnasium();
            se.coachAvailable = request.sports().coachAvailable();
            entity.sports = se;
        }

        // medical
        if (request.medical() != null) {
            SchoolMedicalEntity me = new SchoolMedicalEntity();
            me.medicalRoom = request.medical().medicalRoom();
            me.doctorAvailable = request.medical().doctorAvailable();
            me.nurseAvailable = request.medical().nurseAvailable();
            me.ambulance = request.medical().ambulance();
            me.nearestHospital = request.medical().nearestHospital();
            entity.medical = me;
        }

        // compliance
        if (request.compliance() != null) {
            SchoolComplianceEntity ce = new SchoolComplianceEntity();
            ce.childSafetyPolicy = request.compliance().childSafetyPolicy();
            ce.poshPolicy = request.compliance().poshPolicy();
            ce.antiRaggingPolicy = request.compliance().antiRaggingPolicy();
            ce.disasterManagementPlan = request.compliance().disasterManagementPlan();
            ce.insuranceDetails = request.compliance().insuranceDetails();
            entity.compliance = ce;
        }

        // integrations
        if (request.integrations() != null) {
            SchoolIntegrationsEntity ie = new SchoolIntegrationsEntity();
            ie.paymentGateway = request.integrations().paymentGateway();
            ie.smsProvider = request.integrations().smsProvider();
            ie.whatsappApi = request.integrations().whatsappApi();
            ie.googleWorkspace = request.integrations().googleWorkspace();
            ie.microsoft365 = request.integrations().microsoft365();
            ie.zoomIntegration = request.integrations().zoomIntegration();
            ie.biometricVendor = request.integrations().biometricVendor();
            entity.integrations = ie;
        }

        int calculatedStudents = 0;
        if (request.academicStatistics() != null) {
            int boys = request.academicStatistics().boys() == null ? 0 : request.academicStatistics().boys();
            int girls = request.academicStatistics().girls() == null ? 0 : request.academicStatistics().girls();
            calculatedStudents = boys + girls;
            entity.teachers = request.academicStatistics().teachers() == null ? 0 : request.academicStatistics().teachers();
        }
        if (request.classes() != null && !request.classes().isEmpty()) {
            int classStudentTotal = request.classes().stream()
                .mapToInt(c -> c.currentStrength() == null ? 0 : c.currentStrength())
                .sum();
            if (classStudentTotal > 0) {
                calculatedStudents = classStudentTotal;
            }
        }
        entity.totalStudents = calculatedStudents;
        entity.totalCampuses = 1;

        if (request.principal() != null) {
            entity.schoolAdminName = request.principal().principalName();
            entity.schoolAdminEmail = request.principal().email();
            entity.schoolAdminPhone = request.principal().mobile();
        }

        SchoolEntity saved = schoolRepository.save(entity);
        return new SchoolOnboardingResponse(saved.externalId, saved.schoolCode, saved.schoolName, saved.status, "School created");
    }
}

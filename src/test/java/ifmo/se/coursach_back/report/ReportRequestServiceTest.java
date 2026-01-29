package ifmo.se.coursach_back.report;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import ifmo.se.coursach_back.audit.AuditService;
import ifmo.se.coursach_back.model.Account;
import ifmo.se.coursach_back.model.DonorProfile;
import ifmo.se.coursach_back.model.Notification;
import ifmo.se.coursach_back.model.NotificationDelivery;
import ifmo.se.coursach_back.model.ReportRequest;
import ifmo.se.coursach_back.model.ReportRequestStatus;
import ifmo.se.coursach_back.model.ReportType;
import ifmo.se.coursach_back.model.Role;
import ifmo.se.coursach_back.model.StaffProfile;
import ifmo.se.coursach_back.report.dto.ReportRequestCreateRequest;
import ifmo.se.coursach_back.repository.AccountRepository;
import ifmo.se.coursach_back.repository.DeferralRepository;
import ifmo.se.coursach_back.repository.DonationRepository;
import ifmo.se.coursach_back.repository.DonorProfileRepository;
import ifmo.se.coursach_back.repository.LabTestResultRepository;
import ifmo.se.coursach_back.repository.MedicalCheckRepository;
import ifmo.se.coursach_back.repository.NotificationDeliveryRepository;
import ifmo.se.coursach_back.repository.NotificationRepository;
import ifmo.se.coursach_back.repository.ReportRequestRepository;
import ifmo.se.coursach_back.repository.SampleRepository;
import ifmo.se.coursach_back.repository.StaffProfileRepository;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class ReportRequestServiceTest {
    @Mock private ReportRequestRepository reportRequestRepository;
    @Mock private DonorProfileRepository donorProfileRepository;
    @Mock private StaffProfileRepository staffProfileRepository;
    @Mock private AccountRepository accountRepository;
    @Mock private DonationRepository donationRepository;
    @Mock private MedicalCheckRepository medicalCheckRepository;
    @Mock private DeferralRepository deferralRepository;
    @Mock private SampleRepository sampleRepository;
    @Mock private LabTestResultRepository labTestResultRepository;
    @Mock private NotificationRepository notificationRepository;
    @Mock private NotificationDeliveryRepository notificationDeliveryRepository;
    @Mock private AuditService auditService;

    private ReportRequestService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new ReportRequestService(
                reportRequestRepository,
                donorProfileRepository,
                staffProfileRepository,
                accountRepository,
                donationRepository,
                medicalCheckRepository,
                deferralRepository,
                sampleRepository,
                labTestResultRepository,
                notificationRepository,
                notificationDeliveryRepository,
                new ObjectMapper(),
                auditService
        );
    }

    @Test
    void reportRequestFlowCreatesGeneratesAndSends() {
        UUID requesterAccountId = UUID.randomUUID();
        UUID adminAccountId = UUID.randomUUID();
        UUID donorId = UUID.randomUUID();

        Account requesterAccount = new Account();
        requesterAccount.setId(requesterAccountId);
        Role doctorRole = new Role();
        doctorRole.setCode("DOCTOR");
        requesterAccount.setRoles(Set.of(doctorRole));

        StaffProfile requester = new StaffProfile();
        requester.setId(UUID.randomUUID());
        requester.setAccount(requesterAccount);
        requester.setFullName("Dr. Request");

        StaffProfile admin = new StaffProfile();
        admin.setId(UUID.randomUUID());
        admin.setAccount(new Account());
        admin.getAccount().setId(adminAccountId);
        admin.setFullName("Admin");

        DonorProfile donor = new DonorProfile();
        donor.setId(donorId);
        donor.setFullName("Donor");
        donor.setAccount(new Account());

        ReportRequest persisted = new ReportRequest();
        persisted.setId(UUID.randomUUID());
        persisted.setDonor(donor);
        persisted.setRequestedBy(requester);
        persisted.setReportType(ReportType.DONOR_SUMMARY);
        persisted.setStatus(ReportRequestStatus.REQUESTED);

        when(staffProfileRepository.findByAccountId(requesterAccountId)).thenReturn(Optional.of(requester));
        when(donorProfileRepository.findById(donorId)).thenReturn(Optional.of(donor));
        when(reportRequestRepository.save(any(ReportRequest.class))).thenAnswer(invocation -> {
            ReportRequest req = invocation.getArgument(0);
            if (req.getId() == null) {
                req.setId(persisted.getId());
            }
            return req;
        });

        var created = service.createRequest(requesterAccountId,
                new ReportRequestCreateRequest(donorId, ReportType.DONOR_SUMMARY, null));
        assertEquals(ReportRequestStatus.REQUESTED, created.status());

        when(staffProfileRepository.findByAccountId(adminAccountId)).thenReturn(Optional.of(admin));
        when(reportRequestRepository.findById(persisted.getId())).thenReturn(Optional.of(persisted));
        when(donationRepository.findByDonorAccountId(any(UUID.class))).thenReturn(List.of());
        when(medicalCheckRepository.findTopByVisit_Booking_Donor_IdOrderByDecisionAtDesc(any(UUID.class)))
                .thenReturn(Optional.empty());
        when(deferralRepository.findActiveDeferral(any(UUID.class), any()))
                .thenReturn(Optional.empty());

        var taken = service.takeRequest(adminAccountId, persisted.getId());
        assertEquals(ReportRequestStatus.IN_PROGRESS, taken.status());

        var generated = service.generateReport(adminAccountId, persisted.getId());
        assertEquals(ReportRequestStatus.READY, generated.status());
        assertNotNull(generated.payload());

        when(notificationRepository.save(any(Notification.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(notificationDeliveryRepository.save(any(NotificationDelivery.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var sent = service.sendReport(adminAccountId, persisted.getId());
        assertEquals(ReportRequestStatus.SENT, sent.status());
    }
}

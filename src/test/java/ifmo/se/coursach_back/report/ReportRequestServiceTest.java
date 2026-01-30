package ifmo.se.coursach_back.report;

import ifmo.se.coursach_back.report.application.ReportRequestService;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import ifmo.se.coursach_back.shared.application.ports.DomainEventPublisher;
import ifmo.se.coursach_back.shared.domain.Account;
import ifmo.se.coursach_back.donor.domain.DonorProfile;
import ifmo.se.coursach_back.notification.domain.Notification;
import ifmo.se.coursach_back.notification.domain.NotificationDelivery;
import ifmo.se.coursach_back.report.domain.ReportRequest;
import ifmo.se.coursach_back.report.domain.ReportRequestStatus;
import ifmo.se.coursach_back.report.domain.ReportType;
import ifmo.se.coursach_back.shared.domain.Role;
import ifmo.se.coursach_back.admin.domain.StaffProfile;
import ifmo.se.coursach_back.report.api.dto.ReportRequestCreateRequest;
import ifmo.se.coursach_back.shared.application.ports.AccountRepositoryPort;
import ifmo.se.coursach_back.medical.application.ports.DeferralRepositoryPort;
import ifmo.se.coursach_back.medical.application.ports.DonationRepositoryPort;
import ifmo.se.coursach_back.donor.application.ports.DonorProfileRepositoryPort;
import ifmo.se.coursach_back.lab.application.ports.LabTestResultRepositoryPort;
import ifmo.se.coursach_back.medical.application.ports.MedicalCheckRepositoryPort;
import ifmo.se.coursach_back.notification.application.ports.NotificationDeliveryRepositoryPort;
import ifmo.se.coursach_back.notification.application.ports.NotificationRepositoryPort;
import ifmo.se.coursach_back.report.application.ports.ReportRequestRepositoryPort;
import ifmo.se.coursach_back.medical.application.ports.SampleRepositoryPort;
import ifmo.se.coursach_back.admin.application.ports.StaffProfileRepositoryPort;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class ReportRequestServiceTest {
    @Mock private ReportRequestRepositoryPort reportRequestRepository;
    @Mock private DonorProfileRepositoryPort donorProfileRepository;
    @Mock private StaffProfileRepositoryPort staffProfileRepository;
    @Mock private AccountRepositoryPort accountRepository;
    @Mock private DonationRepositoryPort donationRepository;
    @Mock private MedicalCheckRepositoryPort medicalCheckRepository;
    @Mock private DeferralRepositoryPort deferralRepository;
    @Mock private SampleRepositoryPort sampleRepository;
    @Mock private LabTestResultRepositoryPort labTestResultRepository;
    @Mock private NotificationRepositoryPort notificationRepository;
    @Mock private NotificationDeliveryRepositoryPort notificationDeliveryRepository;
    @Mock private DomainEventPublisher eventPublisher;

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
                eventPublisher
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
        when(medicalCheckRepository.findLatestByDonorId(any(UUID.class)))
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

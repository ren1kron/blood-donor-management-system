package ifmo.se.coursach_back.medical;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ifmo.se.coursach_back.medical.dto.DonationRequest;
import ifmo.se.coursach_back.model.AppointmentSlot;
import ifmo.se.coursach_back.model.Booking;
import ifmo.se.coursach_back.model.Donation;
import ifmo.se.coursach_back.model.DonorProfile;
import ifmo.se.coursach_back.model.MedicalCheck;
import ifmo.se.coursach_back.model.CollectionSession;
import ifmo.se.coursach_back.model.CollectionSessionStatus;
import ifmo.se.coursach_back.model.SlotPurpose;
import ifmo.se.coursach_back.model.StaffProfile;
import ifmo.se.coursach_back.model.Visit;
import ifmo.se.coursach_back.audit.AuditService;
import ifmo.se.coursach_back.repository.AdverseReactionRepository;
import ifmo.se.coursach_back.repository.BookingRepository;
import ifmo.se.coursach_back.repository.CollectionSessionRepository;
import ifmo.se.coursach_back.repository.DeferralRepository;
import ifmo.se.coursach_back.repository.DonationRepository;
import ifmo.se.coursach_back.repository.DonorProfileRepository;
import ifmo.se.coursach_back.repository.LabExaminationRequestRepository;
import ifmo.se.coursach_back.repository.MedicalCheckRepository;
import ifmo.se.coursach_back.repository.NotificationDeliveryRepository;
import ifmo.se.coursach_back.repository.NotificationRepository;
import ifmo.se.coursach_back.repository.SampleRepository;
import ifmo.se.coursach_back.repository.StaffProfileRepository;
import ifmo.se.coursach_back.repository.VisitRepository;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

class MedicalWorkflowServiceDonationTest {
    @Mock private BookingRepository bookingRepository;
    @Mock private VisitRepository visitRepository;
    @Mock private MedicalCheckRepository medicalCheckRepository;
    @Mock private DeferralRepository deferralRepository;
    @Mock private DonationRepository donationRepository;
    @Mock private SampleRepository sampleRepository;
    @Mock private AdverseReactionRepository adverseReactionRepository;
    @Mock private DonorProfileRepository donorProfileRepository;
    @Mock private StaffProfileRepository staffProfileRepository;
    @Mock private LabExaminationRequestRepository labExaminationRequestRepository;
    @Mock private NotificationRepository notificationRepository;
    @Mock private NotificationDeliveryRepository notificationDeliveryRepository;
    @Mock private CollectionSessionRepository collectionSessionRepository;
    @Mock private AuditService auditService;

    private MedicalWorkflowService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new MedicalWorkflowService(
                bookingRepository,
                visitRepository,
                medicalCheckRepository,
                deferralRepository,
                donationRepository,
                sampleRepository,
                adverseReactionRepository,
                donorProfileRepository,
                staffProfileRepository,
                labExaminationRequestRepository,
                notificationRepository,
                notificationDeliveryRepository,
                collectionSessionRepository,
                auditService
        );
    }

    @Test
    void registerDonationMarksPublishedAndActivatesDonor() {
        UUID accountId = UUID.randomUUID();
        UUID visitId = UUID.randomUUID();

        DonorProfile donor = new DonorProfile();
        donor.setId(UUID.randomUUID());
        donor.setDonorStatus("POTENTIAL");

        AppointmentSlot slot = new AppointmentSlot();
        slot.setPurpose(SlotPurpose.DONATION);

        Booking booking = new Booking();
        booking.setDonor(donor);
        booking.setSlot(slot);

        Visit visit = new Visit();
        visit.setId(visitId);
        visit.setBooking(booking);

        StaffProfile doctor = new StaffProfile();
        doctor.setId(UUID.randomUUID());

        MedicalCheck check = new MedicalCheck();
        check.setDecision("ADMITTED");
        check.setDecisionAt(OffsetDateTime.now());

        when(staffProfileRepository.findByAccountId(accountId)).thenReturn(Optional.of(doctor));
        when(visitRepository.findById(visitId)).thenReturn(Optional.of(visit));
        when(donationRepository.findByVisit_Id(visitId)).thenReturn(Optional.empty());
        CollectionSession session = new CollectionSession();
        session.setStatus(CollectionSessionStatus.PREPARED);
        when(collectionSessionRepository.findByVisit_Id(visitId)).thenReturn(Optional.of(session));
        when(medicalCheckRepository.findTopByVisit_Booking_Donor_IdOrderByDecisionAtDesc(donor.getId()))
                .thenReturn(Optional.of(check));
        when(donationRepository.save(any(Donation.class))).thenAnswer(invocation -> invocation.getArgument(0));

        DonationRequest request = new DonationRequest(null, visitId, "WHOLE_BLOOD", 450, null);
        Donation saved = service.recordDonation(accountId, request);

        assertTrue(saved.isPublished());
        assertNotNull(saved.getPublishedAt());
        verify(donorProfileRepository).save(argThat(p -> "ACTIVE".equalsIgnoreCase(p.getDonorStatus())));
    }

    @Test
    void registerDonationFailsIfAlreadyExists() {
        UUID accountId = UUID.randomUUID();
        UUID visitId = UUID.randomUUID();

        DonorProfile donor = new DonorProfile();
        AppointmentSlot slot = new AppointmentSlot();
        slot.setPurpose(SlotPurpose.DONATION);

        Booking booking = new Booking();
        booking.setDonor(donor);
        booking.setSlot(slot);

        Visit visit = new Visit();
        visit.setId(visitId);
        visit.setBooking(booking);

        StaffProfile doctor = new StaffProfile();
        MedicalCheck check = new MedicalCheck();
        check.setDecision("ADMITTED");

        when(staffProfileRepository.findByAccountId(accountId)).thenReturn(Optional.of(doctor));
        when(visitRepository.findById(visitId)).thenReturn(Optional.of(visit));
        when(medicalCheckRepository.findTopByVisit_Booking_Donor_IdOrderByDecisionAtDesc(donor.getId()))
                .thenReturn(Optional.of(check));
        CollectionSession session = new CollectionSession();
        session.setStatus(CollectionSessionStatus.PREPARED);
        when(collectionSessionRepository.findByVisit_Id(visitId)).thenReturn(Optional.of(session));
        when(donationRepository.findByVisit_Id(visitId)).thenReturn(Optional.of(new Donation()));

        DonationRequest request = new DonationRequest(null, visitId, "WHOLE_BLOOD", 450, null);
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.recordDonation(accountId, request));
        assertEquals(409, ex.getStatusCode().value());
    }

    @Test
    void registerDonationFailsWithoutCollectionSession() {
        UUID accountId = UUID.randomUUID();
        UUID visitId = UUID.randomUUID();

        DonorProfile donor = new DonorProfile();
        AppointmentSlot slot = new AppointmentSlot();
        slot.setPurpose(SlotPurpose.DONATION);

        Booking booking = new Booking();
        booking.setDonor(donor);
        booking.setSlot(slot);

        Visit visit = new Visit();
        visit.setId(visitId);
        visit.setBooking(booking);

        StaffProfile doctor = new StaffProfile();
        MedicalCheck check = new MedicalCheck();
        check.setDecision("ADMITTED");

        when(staffProfileRepository.findByAccountId(accountId)).thenReturn(Optional.of(doctor));
        when(visitRepository.findById(visitId)).thenReturn(Optional.of(visit));
        when(medicalCheckRepository.findTopByVisit_Booking_Donor_IdOrderByDecisionAtDesc(donor.getId()))
                .thenReturn(Optional.of(check));
        when(donationRepository.findByVisit_Id(visitId)).thenReturn(Optional.empty());
        when(collectionSessionRepository.findByVisit_Id(visitId)).thenReturn(Optional.empty());

        DonationRequest request = new DonationRequest(null, visitId, "WHOLE_BLOOD", 450, null);
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.recordDonation(accountId, request));
        assertEquals(409, ex.getStatusCode().value());
    }
}

package ifmo.se.coursach_back.medical;

import ifmo.se.coursach_back.medical.application.MedicalWorkflowService;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ifmo.se.coursach_back.medical.api.dto.DonationRequest;
import ifmo.se.coursach_back.appointment.domain.AppointmentSlot;
import ifmo.se.coursach_back.appointment.domain.Booking;
import ifmo.se.coursach_back.medical.domain.Donation;
import ifmo.se.coursach_back.donor.domain.DonorProfile;
import ifmo.se.coursach_back.donor.domain.DonorStatus;
import ifmo.se.coursach_back.medical.domain.MedicalCheck;
import ifmo.se.coursach_back.medical.domain.MedicalCheckDecision;
import ifmo.se.coursach_back.nurse.domain.CollectionSession;
import ifmo.se.coursach_back.nurse.domain.CollectionSessionStatus;
import ifmo.se.coursach_back.appointment.domain.SlotPurpose;
import ifmo.se.coursach_back.admin.domain.StaffProfile;
import ifmo.se.coursach_back.appointment.domain.Visit;
import ifmo.se.coursach_back.shared.application.ports.DomainEventPublisher;
import ifmo.se.coursach_back.medical.application.ports.AdverseReactionRepositoryPort;
import ifmo.se.coursach_back.appointment.application.ports.BookingRepositoryPort;
import ifmo.se.coursach_back.nurse.application.ports.CollectionSessionRepositoryPort;
import ifmo.se.coursach_back.medical.application.ports.DeferralRepositoryPort;
import ifmo.se.coursach_back.medical.application.ports.DonationRepositoryPort;
import ifmo.se.coursach_back.donor.application.ports.DonorProfileRepositoryPort;
import ifmo.se.coursach_back.lab.application.ports.LabExaminationRequestRepositoryPort;
import ifmo.se.coursach_back.medical.application.ports.MedicalCheckRepositoryPort;
import ifmo.se.coursach_back.medical.application.ports.SampleRepositoryPort;
import ifmo.se.coursach_back.admin.application.ports.StaffProfileRepositoryPort;
import ifmo.se.coursach_back.appointment.application.ports.VisitRepositoryPort;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

class MedicalWorkflowServiceDonationTest {
    @Mock private BookingRepositoryPort bookingRepository;
    @Mock private VisitRepositoryPort visitRepository;
    @Mock private MedicalCheckRepositoryPort medicalCheckRepository;
    @Mock private DeferralRepositoryPort deferralRepository;
    @Mock private DonationRepositoryPort donationRepository;
    @Mock private SampleRepositoryPort sampleRepository;
    @Mock private AdverseReactionRepositoryPort adverseReactionRepository;
    @Mock private DonorProfileRepositoryPort donorProfileRepository;
    @Mock private StaffProfileRepositoryPort staffProfileRepository;
    @Mock private LabExaminationRequestRepositoryPort labExaminationRequestRepository;
    @Mock private CollectionSessionRepositoryPort collectionSessionRepository;
    @Mock private DomainEventPublisher eventPublisher;

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
                collectionSessionRepository,
                eventPublisher
        );
    }

    @Test
    void registerDonationMarksPublishedAndActivatesDonor() {
        UUID accountId = UUID.randomUUID();
        UUID visitId = UUID.randomUUID();

        DonorProfile donor = new DonorProfile();
        donor.setId(UUID.randomUUID());
        donor.setDonorStatus(DonorStatus.POTENTIAL);

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
        check.setDecision(MedicalCheckDecision.ADMITTED);
        check.setDecisionAt(OffsetDateTime.now());

        when(staffProfileRepository.findByAccountId(accountId)).thenReturn(Optional.of(doctor));
        when(visitRepository.findById(visitId)).thenReturn(Optional.of(visit));
        when(donationRepository.findByVisitId(visitId)).thenReturn(Optional.empty());
        CollectionSession session = new CollectionSession();
        session.setStatus(CollectionSessionStatus.PREPARED);
        when(collectionSessionRepository.findByVisitId(visitId)).thenReturn(Optional.of(session));
        when(medicalCheckRepository.findLatestByDonorId(donor.getId()))
                .thenReturn(Optional.of(check));
        when(donationRepository.save(any(Donation.class))).thenAnswer(invocation -> invocation.getArgument(0));

        DonationRequest request = new DonationRequest(null, visitId, "WHOLE_BLOOD", 450, null);
        Donation saved = service.recordDonation(accountId, request);

        assertTrue(saved.isPublished());
        assertNotNull(saved.getPublishedAt());
        verify(donorProfileRepository).save(argThat(p -> p.getDonorStatus() == DonorStatus.ACTIVE));
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
        check.setDecision(MedicalCheckDecision.ADMITTED);

        when(staffProfileRepository.findByAccountId(accountId)).thenReturn(Optional.of(doctor));
        when(visitRepository.findById(visitId)).thenReturn(Optional.of(visit));
        when(medicalCheckRepository.findLatestByDonorId(donor.getId()))
                .thenReturn(Optional.of(check));
        CollectionSession session = new CollectionSession();
        session.setStatus(CollectionSessionStatus.PREPARED);
        when(collectionSessionRepository.findByVisitId(visitId)).thenReturn(Optional.of(session));
        when(donationRepository.findByVisitId(visitId)).thenReturn(Optional.of(new Donation()));

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
        check.setDecision(MedicalCheckDecision.ADMITTED);

        when(staffProfileRepository.findByAccountId(accountId)).thenReturn(Optional.of(doctor));
        when(visitRepository.findById(visitId)).thenReturn(Optional.of(visit));
        when(medicalCheckRepository.findLatestByDonorId(donor.getId()))
                .thenReturn(Optional.of(check));
        when(donationRepository.findByVisitId(visitId)).thenReturn(Optional.empty());
        when(collectionSessionRepository.findByVisitId(visitId)).thenReturn(Optional.empty());

        DonationRequest request = new DonationRequest(null, visitId, "WHOLE_BLOOD", 450, null);
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.recordDonation(accountId, request));
        assertEquals(409, ex.getStatusCode().value());
    }
}

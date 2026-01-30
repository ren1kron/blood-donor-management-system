package ifmo.se.coursach_back.nurse;

import ifmo.se.coursach_back.nurse.application.NurseWorkflowService;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import ifmo.se.coursach_back.audit.application.AuditService;
import ifmo.se.coursach_back.appointment.domain.AppointmentSlot;
import ifmo.se.coursach_back.appointment.domain.Booking;
import ifmo.se.coursach_back.nurse.domain.CollectionSession;
import ifmo.se.coursach_back.nurse.domain.CollectionSessionStatus;
import ifmo.se.coursach_back.appointment.domain.SlotPurpose;
import ifmo.se.coursach_back.admin.domain.StaffProfile;
import ifmo.se.coursach_back.appointment.domain.Visit;
import ifmo.se.coursach_back.nurse.api.dto.CollectionSessionCreateRequest;
import ifmo.se.coursach_back.nurse.api.dto.CollectionSessionResponse;
import ifmo.se.coursach_back.nurse.api.dto.CollectionSessionUpdateRequest;
import ifmo.se.coursach_back.nurse.api.dto.VitalsPayload;
import ifmo.se.coursach_back.appointment.infra.jpa.BookingRepository;
import ifmo.se.coursach_back.nurse.infra.jpa.CollectionSessionRepository;
import ifmo.se.coursach_back.medical.infra.jpa.DonationRepository;
import ifmo.se.coursach_back.medical.infra.jpa.MedicalCheckRepository;
import ifmo.se.coursach_back.admin.infra.jpa.StaffProfileRepository;
import ifmo.se.coursach_back.appointment.infra.jpa.VisitRepository;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class NurseWorkflowServiceTest {
    @Mock private BookingRepository bookingRepository;
    @Mock private VisitRepository visitRepository;
    @Mock private MedicalCheckRepository medicalCheckRepository;
    @Mock private DonationRepository donationRepository;
    @Mock private CollectionSessionRepository collectionSessionRepository;
    @Mock private StaffProfileRepository staffProfileRepository;
    @Mock private AuditService auditService;

    private NurseWorkflowService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new NurseWorkflowService(
                bookingRepository,
                visitRepository,
                medicalCheckRepository,
                donationRepository,
                collectionSessionRepository,
                staffProfileRepository,
                new ObjectMapper(),
                auditService
        );
    }

    @Test
    void nurseCanCreateStartAndCompleteSession() {
        UUID accountId = UUID.randomUUID();
        UUID visitId = UUID.randomUUID();

        StaffProfile nurse = new StaffProfile();
        nurse.setId(UUID.randomUUID());
        nurse.setFullName("Nurse Test");

        AppointmentSlot slot = new AppointmentSlot();
        slot.setPurpose(SlotPurpose.DONATION);
        Booking booking = new Booking();
        booking.setSlot(slot);

        Visit visit = new Visit();
        visit.setId(visitId);
        visit.setBooking(booking);

        when(staffProfileRepository.findByAccountId(accountId)).thenReturn(Optional.of(nurse));
        when(visitRepository.findById(visitId)).thenReturn(Optional.of(visit));
        when(collectionSessionRepository.findByVisit_Id(visitId)).thenReturn(Optional.empty());
        when(collectionSessionRepository.save(any(CollectionSession.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        CollectionSessionCreateRequest createRequest = new CollectionSessionCreateRequest(
                visitId,
                null,
                new VitalsPayload(120, 80, 72, 36.6, "ok"),
                "notes"
        );

        CollectionSessionResponse created = service.createSession(accountId, createRequest);
        assertEquals(CollectionSessionStatus.PREPARED, created.status());
        assertEquals(nurse.getId(), created.nurseId());

        CollectionSession existing = new CollectionSession();
        existing.setId(UUID.randomUUID());
        existing.setVisit(visit);
        existing.setStatus(CollectionSessionStatus.PREPARED);
        when(collectionSessionRepository.findById(existing.getId())).thenReturn(Optional.of(existing));

        CollectionSessionResponse started = service.startSession(accountId, existing.getId(),
                new CollectionSessionUpdateRequest(createRequest.preVitals(), null, null, null, null));
        assertEquals(CollectionSessionStatus.IN_PROGRESS, started.status());
        assertNotNull(started.startedAt());

        CollectionSessionResponse completed = service.completeSession(accountId, existing.getId(),
                new CollectionSessionUpdateRequest(null, new VitalsPayload(118, 78, 70, 36.7, "ok"),
                        "done", null, null));
        assertEquals(CollectionSessionStatus.COMPLETED, completed.status());
        assertNotNull(completed.endedAt());
    }
}

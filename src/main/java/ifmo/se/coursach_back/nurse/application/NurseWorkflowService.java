package ifmo.se.coursach_back.nurse.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ifmo.se.coursach_back.audit.application.AuditService;
import ifmo.se.coursach_back.exception.BadRequestException;
import ifmo.se.coursach_back.exception.NotFoundException;
import ifmo.se.coursach_back.medical.api.dto.ScheduledDonorResponse;
import ifmo.se.coursach_back.appointment.domain.Booking;
import ifmo.se.coursach_back.appointment.domain.BookingStatus;
import ifmo.se.coursach_back.nurse.domain.CollectionSession;
import ifmo.se.coursach_back.nurse.domain.CollectionSessionStatus;
import ifmo.se.coursach_back.medical.domain.Donation;
import ifmo.se.coursach_back.medical.domain.MedicalCheck;
import ifmo.se.coursach_back.appointment.domain.SlotPurpose;
import ifmo.se.coursach_back.admin.domain.StaffProfile;
import ifmo.se.coursach_back.appointment.domain.Visit;
import ifmo.se.coursach_back.nurse.api.dto.CollectionSessionCreateRequest;
import ifmo.se.coursach_back.nurse.api.dto.CollectionSessionResponse;
import ifmo.se.coursach_back.nurse.api.dto.CollectionSessionUpdateRequest;
import ifmo.se.coursach_back.nurse.api.dto.VitalsPayload;
import ifmo.se.coursach_back.appointment.application.ports.BookingRepositoryPort;
import ifmo.se.coursach_back.nurse.application.ports.CollectionSessionRepositoryPort;
import ifmo.se.coursach_back.medical.application.ports.DonationRepositoryPort;
import ifmo.se.coursach_back.medical.application.ports.MedicalCheckRepositoryPort;
import ifmo.se.coursach_back.admin.application.ports.StaffProfileRepositoryPort;
import ifmo.se.coursach_back.appointment.application.ports.VisitRepositoryPort;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class NurseWorkflowService {
    private final BookingRepositoryPort bookingRepository;
    private final VisitRepositoryPort visitRepository;
    private final MedicalCheckRepositoryPort medicalCheckRepository;
    private final DonationRepositoryPort donationRepository;
    private final CollectionSessionRepositoryPort collectionSessionRepository;
    private final StaffProfileRepositoryPort staffProfileRepository;
    private final ObjectMapper objectMapper;
    private final AuditService auditService;

    public List<ScheduledDonorResponse> listDonationQueue(OffsetDateTime from) {
        OffsetDateTime start = from == null ? OffsetDateTime.now().minusHours(2) : from;
        List<Booking> bookings = bookingRepository.findByStatusesAndPurposeAfter(
                List.of(BookingStatus.BOOKED, BookingStatus.CONFIRMED),
                SlotPurpose.DONATION,
                start);
        List<UUID> bookingIds = bookings.stream().map(Booking::getId).toList();
        Map<UUID, Visit> visitsByBooking = loadVisitsByBookingIds(bookingIds);

        List<UUID> visitIds = visitsByBooking.values().stream().map(Visit::getId).toList();
        Map<UUID, MedicalCheck> checksByVisit = loadMedicalChecksByVisitIds(visitIds);
        Map<UUID, Donation> donationsByVisit = loadDonationsByVisitIds(visitIds);
        Map<UUID, CollectionSession> sessionsByVisit = loadSessionsByVisitIds(visitIds);
        Map<UUID, MedicalCheck> latestChecksByDonor = loadLatestChecksByDonorIds(bookings);

        return bookings.stream()
                .map(booking -> {
                    Visit visit = visitsByBooking.get(booking.getId());
                    MedicalCheck check = visit != null ? checksByVisit.get(visit.getId()) : null;
                    if (check == null) {
                        check = latestChecksByDonor.get(booking.getDonor().getId());
                    }
                    Donation donation = visit != null ? donationsByVisit.get(visit.getId()) : null;
                    CollectionSession session = visit != null ? sessionsByVisit.get(visit.getId()) : null;
                    return ScheduledDonorResponse.from(booking, visit, check, donation, session);
                })
                .toList();
    }

    @Transactional
    public CollectionSessionResponse createSession(UUID accountId, CollectionSessionCreateRequest request) {
        StaffProfile nurse = requireStaff(accountId);
        Visit visit = resolveVisit(request.visitId(), request.bookingId());
        Booking booking = visit.getBooking();
        if (booking.getSlot().getPurpose() != SlotPurpose.DONATION) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Visit is not for donation");
        }

        CollectionSession existing = collectionSessionRepository.findByVisitId(visit.getId()).orElse(null);
        if (existing != null) {
            return toResponse(existing);
        }

        CollectionSession session = new CollectionSession();
        session.setVisit(visit);
        session.setNurse(nurse);
        session.setStatus(CollectionSessionStatus.PREPARED);
        session.setPreVitalsJson(toJson(request.preVitals()));
        session.setNotes(normalize(request.notes()));
        CollectionSession saved = collectionSessionRepository.save(session);

        auditService.log(accountId, "COLLECTION_SESSION_CREATED", "CollectionSession", saved.getId(),
                Map.of("visitId", visit.getId()));
        return toResponse(saved);
    }

    @Transactional
    public CollectionSessionResponse startSession(UUID accountId, UUID sessionId, CollectionSessionUpdateRequest request) {
        StaffProfile nurse = requireStaff(accountId);
        CollectionSession session = collectionSessionRepository.findById(sessionId)
                .orElseThrow(() -> new NotFoundException("Collection session not found"));
        if (session.getStatus() == CollectionSessionStatus.COMPLETED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Session already completed");
        }
        if (session.getStatus() == CollectionSessionStatus.ABORTED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Session is aborted");
        }
        session.setNurse(nurse);
        session.setStatus(CollectionSessionStatus.IN_PROGRESS);
        if (session.getStartedAt() == null) {
            session.setStartedAt(OffsetDateTime.now());
        }
        applyUpdate(session, request);
        CollectionSession saved = collectionSessionRepository.save(session);
        auditService.log(accountId, "COLLECTION_SESSION_STARTED", "CollectionSession", saved.getId(), null);
        return toResponse(saved);
    }

    @Transactional
    public CollectionSessionResponse completeSession(UUID accountId, UUID sessionId, CollectionSessionUpdateRequest request) {
        StaffProfile nurse = requireStaff(accountId);
        CollectionSession session = collectionSessionRepository.findById(sessionId)
                .orElseThrow(() -> new NotFoundException("Collection session not found"));
        if (session.getStatus() == CollectionSessionStatus.COMPLETED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Session already completed");
        }
        if (session.getStatus() == CollectionSessionStatus.ABORTED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Session is aborted");
        }
        session.setNurse(nurse);
        if (session.getStartedAt() == null) {
            session.setStartedAt(OffsetDateTime.now());
        }
        session.setStatus(CollectionSessionStatus.COMPLETED);
        session.setEndedAt(OffsetDateTime.now());
        applyUpdate(session, request);
        CollectionSession saved = collectionSessionRepository.save(session);
        
        auditService.log(accountId, "COLLECTION_SESSION_COMPLETED", "CollectionSession", saved.getId(), null);
        return toResponse(saved);
    }

    @Transactional
    public CollectionSessionResponse abortSession(UUID accountId, UUID sessionId, CollectionSessionUpdateRequest request) {
        StaffProfile nurse = requireStaff(accountId);
        CollectionSession session = collectionSessionRepository.findById(sessionId)
                .orElseThrow(() -> new NotFoundException("Collection session not found"));
        if (session.getStatus() == CollectionSessionStatus.COMPLETED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Session already completed");
        }
        session.setNurse(nurse);
        session.setStatus(CollectionSessionStatus.ABORTED);
        session.setEndedAt(OffsetDateTime.now());
        applyUpdate(session, request);
        CollectionSession saved = collectionSessionRepository.save(session);
        auditService.log(accountId, "COLLECTION_SESSION_ABORTED", "CollectionSession", saved.getId(), null);
        return toResponse(saved);
    }

    public CollectionSessionResponse getSession(UUID sessionId) {
        CollectionSession session = collectionSessionRepository.findById(sessionId)
                .orElseThrow(() -> new NotFoundException("Collection session not found"));
        return toResponse(session);
    }

    private void applyUpdate(CollectionSession session, CollectionSessionUpdateRequest request) {
        if (request == null) {
            return;
        }
        if (request.preVitals() != null) {
            session.setPreVitalsJson(toJson(request.preVitals()));
        }
        if (request.postVitals() != null) {
            session.setPostVitalsJson(toJson(request.postVitals()));
        }
        if (request.notes() != null) {
            session.setNotes(normalize(request.notes()));
        }
        if (request.complications() != null) {
            session.setComplications(normalize(request.complications()));
        }
        if (request.interruptionReason() != null) {
            session.setInterruptionReason(normalize(request.interruptionReason()));
        }
    }

    private Visit resolveVisit(UUID visitId, UUID bookingId) {
        if (visitId != null) {
            return visitRepository.findById(visitId)
                    .orElseThrow(() -> new NotFoundException("Visit not found"));
        }
        if (bookingId == null) {
            throw new BadRequestException("visitId or bookingId is required");
        }
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found"));
        return visitRepository.findByBookingId(bookingId)
                .orElseGet(() -> visitRepository.save(newVisit(booking)));
    }

    private Visit newVisit(Booking booking) {
        Visit visit = new Visit();
        visit.setBooking(booking);
        return visit;
    }

    private StaffProfile requireStaff(UUID accountId) {
        return staffProfileRepository.findByAccountId(accountId)
                .orElseThrow(() -> new NotFoundException("Staff profile not found"));
    }

    private Map<UUID, Visit> loadVisitsByBookingIds(List<UUID> bookingIds) {
        if (bookingIds.isEmpty()) {
            return new HashMap<>();
        }
        return visitRepository.findByBookingIds(bookingIds).stream()
                .collect(Collectors.toMap(visit -> visit.getBooking().getId(), visit -> visit));
    }

    private Map<UUID, MedicalCheck> loadMedicalChecksByVisitIds(List<UUID> visitIds) {
        if (visitIds.isEmpty()) {
            return new HashMap<>();
        }
        return medicalCheckRepository.findByVisitIds(visitIds).stream()
                .collect(Collectors.toMap(check -> check.getVisit().getId(), check -> check));
    }

    private Map<UUID, Donation> loadDonationsByVisitIds(List<UUID> visitIds) {
        if (visitIds.isEmpty()) {
            return new HashMap<>();
        }
        return donationRepository.findByVisitIds(visitIds).stream()
                .collect(Collectors.toMap(donation -> donation.getVisit().getId(), donation -> donation));
    }

    private Map<UUID, CollectionSession> loadSessionsByVisitIds(List<UUID> visitIds) {
        if (visitIds.isEmpty()) {
            return new HashMap<>();
        }
        return collectionSessionRepository.findByVisitIds(visitIds).stream()
                .collect(Collectors.toMap(session -> session.getVisit().getId(), session -> session));
    }

    private Map<UUID, MedicalCheck> loadLatestChecksByDonorIds(List<Booking> bookings) {
        List<UUID> donorIds = bookings.stream()
                .map(booking -> booking.getDonor().getId())
                .distinct()
                .toList();
        if (donorIds.isEmpty()) {
            return new HashMap<>();
        }
        return medicalCheckRepository.findLatestByDonorIds(donorIds).stream()
                .collect(Collectors.toMap(
                        check -> check.getVisit().getBooking().getDonor().getId(),
                        check -> check,
                        (first, second) -> {
                            if (first.getDecisionAt() == null) {
                                return second;
                            }
                            if (second.getDecisionAt() == null) {
                                return first;
                            }
                            return first.getDecisionAt().isAfter(second.getDecisionAt()) ? first : second;
                        }
                ));
    }

    private CollectionSessionResponse toResponse(CollectionSession session) {
        return new CollectionSessionResponse(
                session.getId(),
                session.getVisit().getId(),
                session.getNurse() != null ? session.getNurse().getId() : null,
                session.getNurse() != null ? session.getNurse().getFullName() : null,
                session.getStatus(),
                session.getStartedAt(),
                session.getEndedAt(),
                parseVitals(session.getPreVitalsJson()),
                parseVitals(session.getPostVitalsJson()),
                session.getNotes(),
                session.getComplications(),
                session.getInterruptionReason(),
                session.getCreatedAt(),
                session.getUpdatedAt()
        );
    }

    private VitalsPayload parseVitals(String json) {
        if (json == null || json.isBlank()) {
            return null;
        }
        try {
            return objectMapper.readValue(json, VitalsPayload.class);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    private String toJson(VitalsPayload payload) {
        if (payload == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            throw new BadRequestException("Invalid vitals payload");
        }
    }

    private String normalize(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}

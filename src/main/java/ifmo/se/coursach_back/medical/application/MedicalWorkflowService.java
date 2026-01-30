package ifmo.se.coursach_back.medical.application;

import ifmo.se.coursach_back.medical.api.dto.AdverseReactionRequest;
import ifmo.se.coursach_back.medical.api.dto.DeferralRequest;
import ifmo.se.coursach_back.medical.api.dto.DonationRequest;
import ifmo.se.coursach_back.medical.api.dto.ExaminationDecisionRequest;
import ifmo.se.coursach_back.medical.api.dto.MedicalCheckRequest;
import ifmo.se.coursach_back.medical.api.dto.ReviewExaminationRequest;
import ifmo.se.coursach_back.medical.api.dto.SampleRequest;
import ifmo.se.coursach_back.medical.domain.AdverseReaction;
import ifmo.se.coursach_back.appointment.domain.Booking;
import ifmo.se.coursach_back.appointment.domain.BookingStatus;
import ifmo.se.coursach_back.nurse.domain.CollectionSession;
import ifmo.se.coursach_back.nurse.domain.CollectionSessionStatus;
import ifmo.se.coursach_back.medical.domain.Deferral;
import ifmo.se.coursach_back.medical.domain.DeferralType;
import ifmo.se.coursach_back.medical.domain.Donation;
import ifmo.se.coursach_back.donor.domain.DonorProfile;
import ifmo.se.coursach_back.donor.domain.DonorStatus;
import ifmo.se.coursach_back.lab.domain.LabExaminationRequest;
import ifmo.se.coursach_back.lab.domain.LabExaminationStatus;
import ifmo.se.coursach_back.medical.domain.MedicalCheck;
import ifmo.se.coursach_back.medical.domain.MedicalCheckDecision;
import ifmo.se.coursach_back.medical.domain.Sample;
import ifmo.se.coursach_back.medical.domain.SampleStatus;
import ifmo.se.coursach_back.appointment.domain.SlotPurpose;
import ifmo.se.coursach_back.admin.domain.StaffProfile;
import ifmo.se.coursach_back.appointment.domain.Visit;
import ifmo.se.coursach_back.shared.application.ports.DomainEventPublisher;
import ifmo.se.coursach_back.shared.domain.event.AuditDomainEvent;
import ifmo.se.coursach_back.shared.domain.event.NotificationDomainEvent;
import ifmo.se.coursach_back.notification.domain.NotificationTopics;
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
public class MedicalWorkflowService {
    private final BookingRepositoryPort bookingRepository;
    private final VisitRepositoryPort visitRepository;
    private final MedicalCheckRepositoryPort medicalCheckRepository;
    private final DeferralRepositoryPort deferralRepository;
    private final DonationRepositoryPort donationRepository;
    private final SampleRepositoryPort sampleRepository;
    private final AdverseReactionRepositoryPort adverseReactionRepository;
    private final DonorProfileRepositoryPort donorProfileRepository;
    private final StaffProfileRepositoryPort staffProfileRepository;
    private final LabExaminationRequestRepositoryPort labExaminationRequestRepository;
    private final CollectionSessionRepositoryPort collectionSessionRepository;
    private final DomainEventPublisher eventPublisher;

    public List<Booking> listScheduledBookings(OffsetDateTime from) {
        return bookingRepository.findByStatusInAndSlot_PurposeAndSlot_StartAtAfterOrderBySlot_StartAtAsc(
                List.of(BookingStatus.BOOKED, BookingStatus.CONFIRMED),
                SlotPurpose.DONATION,
                from);
    }

    public List<Booking> listConfirmedExaminationBookings(OffsetDateTime from) {
        return bookingRepository.findByStatusInAndSlot_PurposeAndSlot_StartAtAfterOrderBySlot_StartAtAsc(
                List.of(BookingStatus.CONFIRMED),
                SlotPurpose.EXAMINATION,
                from);
    }

    public Map<UUID, Visit> loadVisitsByBookingIds(List<UUID> bookingIds) {
        if (bookingIds.isEmpty()) {
            return new HashMap<>();
        }
        return visitRepository.findByBooking_IdIn(bookingIds).stream()
                .collect(Collectors.toMap(visit -> visit.getBooking().getId(), visit -> visit));
    }
    
    public Map<UUID, MedicalCheck> loadMedicalChecksByVisitIds(List<UUID> visitIds) {
        if (visitIds.isEmpty()) {
            return new HashMap<>();
        }
        return medicalCheckRepository.findByVisit_IdIn(visitIds).stream()
                .collect(Collectors.toMap(check -> check.getVisit().getId(), check -> check));
    }
    
    public Map<UUID, Donation> loadDonationsByVisitIds(List<UUID> visitIds) {
        if (visitIds.isEmpty()) {
            return new HashMap<>();
        }
        return donationRepository.findByVisit_IdIn(visitIds).stream()
                .collect(Collectors.toMap(donation -> donation.getVisit().getId(), donation -> donation));
    }

    public Map<UUID, CollectionSession> loadCollectionSessionsByVisitIds(List<UUID> visitIds) {
        if (visitIds.isEmpty()) {
            return new HashMap<>();
        }
        return collectionSessionRepository.findByVisit_IdIn(visitIds).stream()
                .collect(Collectors.toMap(session -> session.getVisit().getId(), session -> session));
    }

    public Map<UUID, LabExaminationRequest> loadLabRequestsByVisitIds(List<UUID> visitIds) {
        if (visitIds.isEmpty()) {
            return new HashMap<>();
        }
        return labExaminationRequestRepository.findByVisit_IdIn(visitIds).stream()
                .collect(Collectors.toMap(request -> request.getVisit().getId(), request -> request));
    }

    public List<MedicalCheck> listPendingExaminations() {
        return medicalCheckRepository.findByStatusOrderBySubmittedAtAsc(MedicalCheckDecision.PENDING_REVIEW);
    }
    
    @Transactional
    public MedicalCheckResult reviewExamination(UUID accountId, ReviewExaminationRequest request) {
        StaffProfile doctor = requireStaff(accountId);
        
        MedicalCheck check = medicalCheckRepository.findById(request.examinationId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Examination not found"));

        LabExaminationRequest labRequest = labExaminationRequestRepository.findByVisit_Id(check.getVisit().getId())
                .orElse(null);
        if (labRequest == null || !LabExaminationStatus.COMPLETED.equals(labRequest.getStatus())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Lab examination is not completed");
        }
        
        if (check.getStatus() != MedicalCheckDecision.PENDING_REVIEW) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Examination has already been reviewed");
        }
        
        MedicalCheckDecision decision = parseDecision(request.decision());
        if (decision != MedicalCheckDecision.ADMITTED && decision != MedicalCheckDecision.REFUSED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Decision must be ADMITTED or REFUSED");
        }
        if (decision == MedicalCheckDecision.REFUSED && request.deferral() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Deferral is required when decision is REFUSED");
        }
        if (decision == MedicalCheckDecision.ADMITTED && request.deferral() != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Deferral is not allowed when decision is ADMITTED");
        }
        
        check.setPerformedBy(doctor);
        check.setDecision(decision);
        check.setStatus(decision);
        check.setDecisionAt(OffsetDateTime.now());
        
        MedicalCheck saved = medicalCheckRepository.save(check);
        
        Deferral savedDeferral = null;
        if (request.deferral() != null) {
            validateDeferral(request.deferral());
            Deferral deferral = new Deferral();
            deferral.setDonor(check.getVisit().getBooking().getDonor());
            deferral.setCreatedFromCheck(saved);
            deferral.setDeferralType(parseDeferralType(request.deferral().deferralType()));
            deferral.setReason(request.deferral().reason());
            deferral.setEndsAt(request.deferral().endsAt());
            savedDeferral = deferralRepository.save(deferral);
        }
        
        if (decision == MedicalCheckDecision.ADMITTED) {
            sendDonationReadyNotification(check.getVisit().getBooking().getDonor());
        } else {
            sendDeferralNotification(check.getVisit().getBooking().getDonor(), request.deferral());
        }

        eventPublisher.publish(AuditDomainEvent.of(accountId, "MEDICAL_CHECK_DECISION", "MedicalCheck", saved.getId(),
                Map.of("decision", decision.name())));
        
        return new MedicalCheckResult(saved, savedDeferral);
    }

    public record MedicalCheckResult(MedicalCheck check, Deferral deferral) {
    }

    @Transactional
    public MedicalCheckResult recordMedicalCheck(UUID accountId, MedicalCheckRequest request) {
        StaffProfile staff = requireStaff(accountId);
        Visit visit = resolveVisit(request.bookingId(), request.visitId());

        LabExaminationRequest labRequest = labExaminationRequestRepository.findByVisit_Id(visit.getId())
                .orElse(null);
        if (labRequest == null || labRequest.getStatus() != LabExaminationStatus.COMPLETED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Lab examination is not completed");
        }

        MedicalCheckDecision decision = parseDecision(request.decision());
        if (decision != MedicalCheckDecision.ADMITTED && decision != MedicalCheckDecision.REFUSED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Decision must be ADMITTED or REFUSED");
        }
        if (decision == MedicalCheckDecision.REFUSED && request.deferral() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Deferral is required when decision is REFUSED");
        }
        if (decision == MedicalCheckDecision.ADMITTED && request.deferral() != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Deferral is not allowed when decision is ADMITTED");
        }

        MedicalCheck check = medicalCheckRepository.findByVisit_Id(visit.getId()).orElseGet(MedicalCheck::new);
        check.setVisit(visit);
        check.setPerformedBy(staff);
        check.setWeightKg(request.weightKg());
        check.setHemoglobinGl(request.hemoglobinGl());
        check.setSystolicMmhg(request.systolicMmhg());
        check.setDiastolicMmhg(request.diastolicMmhg());
        check.setDecision(decision);
        check.setStatus(decision);
        check.setDecisionAt(OffsetDateTime.now());

        MedicalCheck saved = medicalCheckRepository.save(check);
        DeferralRequest deferralRequest = request.deferral();
        Deferral savedDeferral = null;
        if (deferralRequest != null) {
            validateDeferral(deferralRequest);
            Deferral deferral = new Deferral();
            deferral.setDonor(visit.getBooking().getDonor());
            deferral.setCreatedFromCheck(saved);
            deferral.setDeferralType(parseDeferralType(deferralRequest.deferralType()));
            deferral.setReason(deferralRequest.reason());
            deferral.setEndsAt(deferralRequest.endsAt());
            savedDeferral = deferralRepository.save(deferral);
        }

        if (decision == MedicalCheckDecision.ADMITTED) {
            sendDonationReadyNotification(visit.getBooking().getDonor());
        }

        eventPublisher.publish(AuditDomainEvent.of(accountId, "MEDICAL_CHECK_DECISION", "MedicalCheck", saved.getId(),
                Map.of("decision", decision.name())));

        return new MedicalCheckResult(saved, savedDeferral);
    }

    @Transactional
    public LabExaminationRequest createLabRequest(UUID accountId, UUID visitId) {
        StaffProfile doctor = requireStaff(accountId);
        Visit visit = visitRepository.findById(visitId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Visit not found"));

        Booking booking = visit.getBooking();
        if (booking.getSlot().getPurpose() != SlotPurpose.EXAMINATION) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Visit is not for examination");
        }
        if (!BookingStatus.CONFIRMED.equals(booking.getStatus())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Booking is not confirmed");
        }

        LabExaminationRequest existing = labExaminationRequestRepository.findByVisit_Id(visitId).orElse(null);
        if (existing != null) {
            return existing;
        }

        LabExaminationRequest request = new LabExaminationRequest();
        request.setVisit(visit);
        request.setRequestedBy(doctor);
        request.setRequestedAt(OffsetDateTime.now());
        request.setStatus(LabExaminationStatus.REQUESTED);
        return labExaminationRequestRepository.save(request);
    }

    @Transactional
    public MedicalCheckResult decideExamination(UUID accountId, UUID visitId, ExaminationDecisionRequest request) {
        StaffProfile doctor = requireStaff(accountId);
        Visit visit = visitRepository.findById(visitId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Visit not found"));

        LabExaminationRequest labRequest = labExaminationRequestRepository.findByVisit_Id(visitId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lab examination request not found"));
        if (labRequest.getStatus() != LabExaminationStatus.COMPLETED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Lab examination is not completed");
        }

        MedicalCheckDecision decision = parseDecision(request.decision());
        if (decision != MedicalCheckDecision.ADMITTED && decision != MedicalCheckDecision.REFUSED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Decision must be ADMITTED or REFUSED");
        }
        if (decision == MedicalCheckDecision.REFUSED && request.deferral() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Deferral is required when decision is REFUSED");
        }
        if (decision == MedicalCheckDecision.ADMITTED && request.deferral() != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Deferral is not allowed when decision is ADMITTED");
        }

        MedicalCheck check = medicalCheckRepository.findByVisit_Id(visit.getId()).orElseGet(MedicalCheck::new);
        check.setVisit(visit);
        check.setPerformedBy(doctor);
        check.setDecision(decision);
        check.setStatus(decision);
        check.setDecisionAt(OffsetDateTime.now());

        MedicalCheck saved = medicalCheckRepository.save(check);
        Deferral savedDeferral = null;
        if (request.deferral() != null) {
            validateDeferral(request.deferral());
            Deferral deferral = new Deferral();
            deferral.setDonor(visit.getBooking().getDonor());
            deferral.setCreatedFromCheck(saved);
            deferral.setDeferralType(parseDeferralType(request.deferral().deferralType()));
            deferral.setReason(request.deferral().reason());
            deferral.setEndsAt(request.deferral().endsAt());
            savedDeferral = deferralRepository.save(deferral);
        }

        if (decision == MedicalCheckDecision.ADMITTED) {
            sendDonationReadyNotification(visit.getBooking().getDonor());
        } else {
            sendDeferralNotification(visit.getBooking().getDonor(), request.deferral());
        }

        eventPublisher.publish(AuditDomainEvent.of(accountId, "MEDICAL_CHECK_DECISION", "MedicalCheck", saved.getId(),
                Map.of("decision", decision.name())));

        return new MedicalCheckResult(saved, savedDeferral);
    }
    
    private void sendDonationReadyNotification(DonorProfile donor) {
        eventPublisher.publish(NotificationDomainEvent.inApp(
                donor,
                NotificationTopics.MEDICAL_CHECK,
                "Вы успешно прошли медосмотр и можете записаться на донацию."
        ));
    }
    
    private void sendDeferralNotification(DonorProfile donor, DeferralRequest deferralRequest) {
        String body = "К сожалению, по результатам медосмотра вы не допущены к донации.";
        if (deferralRequest != null && deferralRequest.reason() != null) {
            body += " Причина: " + deferralRequest.reason();
        }
        if (deferralRequest != null && deferralRequest.endsAt() != null) {
            body += " Повторная запись возможна после " + deferralRequest.endsAt().toLocalDate();
        }
        eventPublisher.publish(NotificationDomainEvent.inApp(
                donor,
                NotificationTopics.DEFERRAL,
                body
        ));
    }

    @Transactional
    public Donation recordDonation(UUID accountId, DonationRequest request) {
        StaffProfile staff = requireStaff(accountId);
        Visit visit = resolveVisit(request.bookingId(), request.visitId());
        Booking booking = visit.getBooking();
        if (booking.getSlot().getPurpose() != SlotPurpose.DONATION) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Visit is not for donation");
        }

        MedicalCheck check = medicalCheckRepository
                .findTopByVisit_Booking_Donor_IdOrderByDecisionAtDesc(booking.getDonor().getId())
                .orElse(null);
        if (check == null || check.getDecision() != MedicalCheckDecision.ADMITTED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Donation not allowed without admission");
        }

        if (donationRepository.findByVisit_Id(visit.getId()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Donation already registered for this visit");
        }

        CollectionSession session = collectionSessionRepository.findByVisit_Id(visit.getId()).orElse(null);
        if (session == null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Collection session is required before donation");
        }
        if (session.getStatus() == CollectionSessionStatus.ABORTED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Collection session was aborted");
        }

        Donation donation = new Donation();
        donation.setVisit(visit);
        donation.setDonationType(request.donationType());
        donation.setVolumeMl(request.volumeMl());
        donation.setPerformedBy(staff);
        if (request.performedAt() != null) {
            donation.setPerformedAt(request.performedAt());
        }
        donation.setPublished(true);
        donation.setPublishedAt(donation.getPerformedAt() != null ? donation.getPerformedAt() : OffsetDateTime.now());

        Donation saved = donationRepository.save(donation);
        
        Sample sample = new Sample();
        sample.setDonation(saved);
        sample.setSampleCode(generateSampleCode());
        sample.setStatus(SampleStatus.NEW);
        sampleRepository.save(sample);
        
        // Mark booking as completed after donation registration
        booking.setStatus(BookingStatus.COMPLETED);
        bookingRepository.save(booking);
        
        activateDonorIfNeeded(booking.getDonor());
        eventPublisher.publish(AuditDomainEvent.of(accountId, "DONATION_REGISTERED", "Donation", saved.getId(),
                Map.of("visitId", visit.getId())));
        return saved;
    }

    @Transactional
    public Donation publishDonation(UUID accountId, UUID donationId) {
        requireStaff(accountId);
        Donation donation = donationRepository.findById(donationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Donation not found"));
        if (!donation.isPublished()) {
            donation.setPublished(true);
            donation.setPublishedAt(OffsetDateTime.now());
            donation = donationRepository.save(donation);
            eventPublisher.publish(AuditDomainEvent.of(accountId, "DONATION_PUBLISHED", "Donation", donation.getId()));
        }
        return donation;
    }

    @Transactional
    public Sample registerSample(SampleRequest request) {
        Donation donation = donationRepository.findById(request.donationId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Donation not found"));
        
        String sampleCode = request.sampleCode();
        if (sampleCode == null || sampleCode.isBlank()) {
            sampleCode = generateSampleCode();
        } else {
            sampleCode = sampleCode.trim();
        }
        
        if (sampleRepository.existsBySampleCode(sampleCode)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Sample code already exists");
        }

        Sample sample = new Sample();
        sample.setDonation(donation);
        sample.setSampleCode(sampleCode);
        sample.setStatus(parseSampleStatus(request.status()));
        sample.setQuarantineReason(request.quarantineReason());
        sample.setRejectionReason(request.rejectionReason());
        return sampleRepository.save(sample);
    }

    @Transactional
    public AdverseReaction registerReaction(UUID accountId, AdverseReactionRequest request) {
        StaffProfile staff = requireStaff(accountId);
        Donation donation = donationRepository.findById(request.donationId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Donation not found"));

        AdverseReaction reaction = new AdverseReaction();
        reaction.setDonation(donation);
        reaction.setReportedBy(staff);
        reaction.setOccurredAt(request.occurredAt());
        reaction.setSeverity(request.severity());
        reaction.setDescription(request.description());
        return adverseReactionRepository.save(reaction);
    }

    @Transactional
    public DonorProfile updateDonorStatus(UUID donorId, String donorStatus) {
        DonorProfile donor = donorProfileRepository.findById(donorId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Donor not found"));
        donor.setDonorStatus(DonorStatus.valueOf(donorStatus.trim().toUpperCase()));
        return donorProfileRepository.save(donor);
    }

    public MedicalCheck findLatestCheckByDonor(UUID donorId) {
        return medicalCheckRepository.findTopByVisit_Booking_Donor_IdOrderByDecisionAtDesc(donorId)
                .orElse(null);
    }

    private Visit resolveVisit(UUID bookingId, UUID visitId) {
        if (visitId != null) {
            return visitRepository.findById(visitId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Visit not found"));
        }
        if (bookingId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "bookingId or visitId is required");
        }

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found"));
        if (BookingStatus.CANCELLED.equals(booking.getStatus())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Booking is cancelled");
        }

        return visitRepository.findByBooking_Id(bookingId)
                .orElseGet(() -> visitRepository.save(newVisit(booking)));
    }

    private Visit newVisit(Booking booking) {
        Visit visit = new Visit();
        visit.setBooking(booking);
        return visit;
    }

    private StaffProfile requireStaff(UUID accountId) {
        return staffProfileRepository.findByAccountId(accountId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Staff profile not found"));
    }

    private void validateDeferral(DeferralRequest request) {
        if (request.endsAt() != null && request.endsAt().isBefore(OffsetDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Deferral end time is in the past");
        }
    }

    private void activateDonorIfNeeded(DonorProfile donor) {
        if (donor == null) {
            return;
        }
        if (donor.getDonorStatus() != DonorStatus.ACTIVE) {
            donor.setDonorStatus(DonorStatus.ACTIVE);
            donorProfileRepository.save(donor);
        }
    }

    private MedicalCheckDecision parseDecision(String value) {
        if (value == null || value.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Decision is required");
        }
        try {
            return MedicalCheckDecision.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid decision: " + value);
        }
    }

    private DeferralType parseDeferralType(String value) {
        if (value == null || value.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Deferral type is required");
        }
        try {
            return DeferralType.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid deferral type: " + value);
        }
    }

    private SampleStatus parseSampleStatus(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return SampleStatus.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid sample status: " + value);
        }
    }
    
    private String generateSampleCode() {
        String datePart = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.BASIC_ISO_DATE);
        String randomPart = UUID.randomUUID().toString().substring(0, 5).toUpperCase();
        return "SAM-" + datePart + "-" + randomPart;
    }
}

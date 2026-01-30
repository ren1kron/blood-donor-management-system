package ifmo.se.coursach_back.medical.application;

import ifmo.se.coursach_back.audit.application.AuditService;
import ifmo.se.coursach_back.shared.application.EntityResolverService;
import ifmo.se.coursach_back.exception.ConflictException;
import ifmo.se.coursach_back.exception.NotFoundException;
import ifmo.se.coursach_back.medical.api.dto.AdverseReactionRequest;
import ifmo.se.coursach_back.medical.api.dto.DonationRequest;
import ifmo.se.coursach_back.medical.domain.AdverseReaction;
import ifmo.se.coursach_back.appointment.domain.Booking;
import ifmo.se.coursach_back.appointment.domain.BookingStatus;
import ifmo.se.coursach_back.nurse.domain.CollectionSession;
import ifmo.se.coursach_back.nurse.domain.CollectionSessionStatus;
import ifmo.se.coursach_back.medical.domain.Donation;
import ifmo.se.coursach_back.donor.domain.DonorProfile;
import ifmo.se.coursach_back.donor.domain.DonorStatus;
import ifmo.se.coursach_back.medical.domain.MedicalCheck;
import ifmo.se.coursach_back.medical.domain.MedicalCheckDecision;
import ifmo.se.coursach_back.medical.domain.Sample;
import ifmo.se.coursach_back.medical.domain.SampleStatus;
import ifmo.se.coursach_back.appointment.domain.SlotPurpose;
import ifmo.se.coursach_back.admin.domain.StaffProfile;
import ifmo.se.coursach_back.appointment.domain.Visit;
import ifmo.se.coursach_back.medical.infra.jpa.AdverseReactionRepository;
import ifmo.se.coursach_back.appointment.infra.jpa.BookingRepository;
import ifmo.se.coursach_back.nurse.infra.jpa.CollectionSessionRepository;
import ifmo.se.coursach_back.medical.infra.jpa.DonationRepository;
import ifmo.se.coursach_back.donor.infra.jpa.DonorProfileRepository;
import ifmo.se.coursach_back.medical.infra.jpa.MedicalCheckRepository;
import ifmo.se.coursach_back.medical.infra.jpa.SampleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.UUID;

/**
 * Service for donation recording and management.
 * Handles donation creation, publication, and adverse reactions.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DonationService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.BASIC_ISO_DATE;

    private final DonationRepository donationRepository;
    private final SampleRepository sampleRepository;
    private final AdverseReactionRepository adverseReactionRepository;
    private final MedicalCheckRepository medicalCheckRepository;
    private final CollectionSessionRepository collectionSessionRepository;
    private final BookingRepository bookingRepository;
    private final DonorProfileRepository donorProfileRepository;
    private final EntityResolverService entityResolver;
    private final AuditService auditService;

    /**
     * Records a new donation for a visit.
     */
    @Transactional
    public Donation recordDonation(UUID accountId, DonationRequest request) {
        StaffProfile staff = entityResolver.requireStaff(accountId);
        Visit visit = entityResolver.resolveVisit(request.bookingId(), request.visitId());
        Booking booking = visit.getBooking();

        validateDonationPurpose(booking);
        validateMedicalAdmission(booking.getDonor().getId());
        validateNoDuplicateDonation(visit.getId());
        validateCollectionSession(visit.getId());

        Donation donation = createDonation(visit, staff, request);
        Donation saved = donationRepository.save(donation);

        createSampleForDonation(saved);
        completeBooking(booking);
        activateDonorIfNeeded(booking.getDonor());

        auditService.log(accountId, "DONATION_REGISTERED", "Donation", saved.getId(),
                Map.of("visitId", visit.getId()));

        log.info("Donation recorded: donationId={}, donorId={}", saved.getId(), booking.getDonor().getId());
        return saved;
    }

    /**
     * Publishes a donation (makes results visible to donor).
     */
    @Transactional
    public Donation publishDonation(UUID accountId, UUID donationId) {
        entityResolver.requireStaff(accountId);

        Donation donation = donationRepository.findById(donationId)
                .orElseThrow(() -> NotFoundException.entity("Donation", donationId));

        if (!donation.isPublished()) {
            donation.setPublished(true);
            donation.setPublishedAt(OffsetDateTime.now());
            donation = donationRepository.save(donation);

            auditService.log(accountId, "DONATION_PUBLISHED", "Donation", donation.getId(), null);
            log.info("Donation published: donationId={}", donationId);
        }

        return donation;
    }

    /**
     * Registers an adverse reaction for a donation.
     */
    @Transactional
    public AdverseReaction registerAdverseReaction(UUID accountId, AdverseReactionRequest request) {
        StaffProfile staff = entityResolver.requireStaff(accountId);

        Donation donation = donationRepository.findById(request.donationId())
                .orElseThrow(() -> NotFoundException.entity("Donation", request.donationId()));

        AdverseReaction reaction = new AdverseReaction();
        reaction.setDonation(donation);
        reaction.setReportedBy(staff);
        reaction.setOccurredAt(request.occurredAt());
        reaction.setSeverity(request.severity());
        reaction.setDescription(request.description());

        AdverseReaction saved = adverseReactionRepository.save(reaction);

        auditService.log(accountId, "ADVERSE_REACTION_REGISTERED", "AdverseReaction", saved.getId(),
                Map.of("donationId", donation.getId(), "severity", request.severity()));

        log.warn("Adverse reaction registered: reactionId={}, donationId={}, severity={}",
                saved.getId(), donation.getId(), request.severity());

        return saved;
    }

    private void validateDonationPurpose(Booking booking) {
        if (booking.getSlot().getPurpose() != SlotPurpose.DONATION) {
            throw new ConflictException("Visit is not scheduled for donation");
        }
    }

    private void validateMedicalAdmission(UUID donorId) {
        MedicalCheck check = medicalCheckRepository
                .findTopByVisit_Booking_Donor_IdOrderByDecisionAtDesc(donorId)
                .orElse(null);

        if (check == null || check.getDecision() != MedicalCheckDecision.ADMITTED) {
            throw new ConflictException("Donation not allowed without medical admission");
        }
    }

    private void validateNoDuplicateDonation(UUID visitId) {
        if (donationRepository.findByVisit_Id(visitId).isPresent()) {
            throw new ConflictException("Donation already registered for this visit");
        }
    }

    private void validateCollectionSession(UUID visitId) {
        CollectionSession session = collectionSessionRepository.findByVisit_Id(visitId)
                .orElseThrow(() -> new ConflictException("Collection session is required before donation"));

        if (session.getStatus() == CollectionSessionStatus.ABORTED) {
            throw new ConflictException("Collection session was aborted");
        }
    }

    private Donation createDonation(Visit visit, StaffProfile staff, DonationRequest request) {
        Donation donation = new Donation();
        donation.setVisit(visit);
        donation.setDonationType(request.donationType());
        donation.setVolumeMl(request.volumeMl());
        donation.setPerformedBy(staff);

        if (request.performedAt() != null) {
            donation.setPerformedAt(request.performedAt());
        }

        donation.setPublished(true);
        donation.setPublishedAt(donation.getPerformedAt() != null
                ? donation.getPerformedAt()
                : OffsetDateTime.now());

        return donation;
    }

    private void createSampleForDonation(Donation donation) {
        Sample sample = new Sample();
        sample.setDonation(donation);
        sample.setSampleCode(generateSampleCode());
        sample.setStatus(SampleStatus.NEW);
        sampleRepository.save(sample);
    }

    private void completeBooking(Booking booking) {
        booking.setStatus(BookingStatus.COMPLETED);
        bookingRepository.save(booking);
    }

    private void activateDonorIfNeeded(DonorProfile donor) {
        if (donor != null && donor.getDonorStatus() != DonorStatus.ACTIVE) {
            donor.setDonorStatus(DonorStatus.ACTIVE);
            donorProfileRepository.save(donor);
            log.info("Donor activated: donorId={}", donor.getId());
        }
    }

    private String generateSampleCode() {
        String datePart = LocalDate.now().format(DATE_FORMATTER);
        String randomPart = UUID.randomUUID().toString().substring(0, 5).toUpperCase();
        return "SAM-" + datePart + "-" + randomPart;
    }
}

package ifmo.se.coursach_back.medical.application;

import ifmo.se.coursach_back.shared.application.EntityResolverService;
import ifmo.se.coursach_back.exception.NotFoundException;
import ifmo.se.coursach_back.medical.api.dto.AdverseReactionRequest;
import ifmo.se.coursach_back.medical.api.dto.DonationRequest;
import ifmo.se.coursach_back.medical.api.dto.ExaminationDecisionRequest;
import ifmo.se.coursach_back.medical.api.dto.MedicalCheckRequest;
import ifmo.se.coursach_back.medical.api.dto.ReviewExaminationRequest;
import ifmo.se.coursach_back.medical.api.dto.SampleRequest;
import ifmo.se.coursach_back.medical.domain.AdverseReaction;
import ifmo.se.coursach_back.appointment.domain.Booking;
import ifmo.se.coursach_back.appointment.domain.BookingStatus;
import ifmo.se.coursach_back.nurse.domain.CollectionSession;
import ifmo.se.coursach_back.medical.domain.Deferral;
import ifmo.se.coursach_back.medical.domain.Donation;
import ifmo.se.coursach_back.donor.domain.DonorProfile;
import ifmo.se.coursach_back.donor.domain.DonorStatus;
import ifmo.se.coursach_back.lab.domain.LabExaminationRequest;
import ifmo.se.coursach_back.lab.domain.LabExaminationStatus;
import ifmo.se.coursach_back.medical.domain.MedicalCheck;
import ifmo.se.coursach_back.medical.domain.Sample;
import ifmo.se.coursach_back.appointment.domain.SlotPurpose;
import ifmo.se.coursach_back.admin.domain.StaffProfile;
import ifmo.se.coursach_back.appointment.domain.Visit;
import ifmo.se.coursach_back.appointment.infra.jpa.BookingRepository;
import ifmo.se.coursach_back.nurse.infra.jpa.CollectionSessionRepository;
import ifmo.se.coursach_back.medical.infra.jpa.DonationRepository;
import ifmo.se.coursach_back.donor.infra.jpa.DonorProfileRepository;
import ifmo.se.coursach_back.lab.infra.jpa.LabExaminationRequestRepository;
import ifmo.se.coursach_back.medical.infra.jpa.MedicalCheckRepository;
import ifmo.se.coursach_back.appointment.infra.jpa.VisitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Facade service for medical workflow operations.
 * Provides a unified entry point for all medical workflow operations
 * while delegating to specialized services.
 * 
 * <p>This class follows the Facade pattern to provide backward compatibility
 * while the underlying implementation is split into focused services.</p>
 */
@Service
@RequiredArgsConstructor
public class MedicalWorkflowFacade {

    // Specialized services
    private final MedicalCheckService medicalCheckService;
    private final DonationService donationService;
    private final SampleService sampleService;
    private final EntityResolverService entityResolver;
    private final LabExaminationService labExaminationService;

    // Repositories for batch operations
    private final BookingRepository bookingRepository;
    private final VisitRepository visitRepository;
    private final MedicalCheckRepository medicalCheckRepository;
    private final DonationRepository donationRepository;
    private final CollectionSessionRepository collectionSessionRepository;
    private final LabExaminationRequestRepository labExaminationRequestRepository;
    private final DonorProfileRepository donorProfileRepository;

    // === Booking and Visit queries ===

    public List<Booking> listScheduledBookings(OffsetDateTime from) {
        return bookingRepository.findByStatusInAndSlot_PurposeAndSlot_StartAtAfterOrderBySlot_StartAtAsc(
                List.of(BookingStatus.BOOKED, BookingStatus.CONFIRMED),
                SlotPurpose.DONATION,
                from
        );
    }

    public List<Booking> listConfirmedExaminationBookings(OffsetDateTime from) {
        return bookingRepository.findByStatusInAndSlot_PurposeAndSlot_StartAtAfterOrderBySlot_StartAtAsc(
                List.of(BookingStatus.CONFIRMED),
                SlotPurpose.EXAMINATION,
                from
        );
    }

    // === Batch loading operations ===

    public Map<UUID, Visit> loadVisitsByBookingIds(List<UUID> bookingIds) {
        if (bookingIds.isEmpty()) {
            return new HashMap<>();
        }
        return visitRepository.findByBooking_IdIn(bookingIds).stream()
                .collect(Collectors.toMap(v -> v.getBooking().getId(), v -> v));
    }

    public Map<UUID, MedicalCheck> loadMedicalChecksByVisitIds(List<UUID> visitIds) {
        if (visitIds.isEmpty()) {
            return new HashMap<>();
        }
        return medicalCheckRepository.findByVisit_IdIn(visitIds).stream()
                .collect(Collectors.toMap(c -> c.getVisit().getId(), c -> c));
    }

    public Map<UUID, Donation> loadDonationsByVisitIds(List<UUID> visitIds) {
        if (visitIds.isEmpty()) {
            return new HashMap<>();
        }
        return donationRepository.findByVisit_IdIn(visitIds).stream()
                .collect(Collectors.toMap(d -> d.getVisit().getId(), d -> d));
    }

    public Map<UUID, CollectionSession> loadCollectionSessionsByVisitIds(List<UUID> visitIds) {
        if (visitIds.isEmpty()) {
            return new HashMap<>();
        }
        return collectionSessionRepository.findByVisit_IdIn(visitIds).stream()
                .collect(Collectors.toMap(s -> s.getVisit().getId(), s -> s));
    }

    public Map<UUID, LabExaminationRequest> loadLabRequestsByVisitIds(List<UUID> visitIds) {
        if (visitIds.isEmpty()) {
            return new HashMap<>();
        }
        return labExaminationRequestRepository.findByVisit_IdIn(visitIds).stream()
                .collect(Collectors.toMap(r -> r.getVisit().getId(), r -> r));
    }

    // === Medical Check operations (delegated) ===

    public List<MedicalCheck> listPendingExaminations() {
        return medicalCheckService.listPendingExaminations();
    }

    @Transactional
    public MedicalCheckService.MedicalCheckResult reviewExamination(UUID accountId, ReviewExaminationRequest request) {
        return medicalCheckService.reviewExamination(accountId, request);
    }

    @Transactional
    public MedicalCheckService.MedicalCheckResult recordMedicalCheck(UUID accountId, MedicalCheckRequest request) {
        return medicalCheckService.recordMedicalCheck(accountId, request);
    }

    @Transactional
    public MedicalCheckService.MedicalCheckResult decideExamination(UUID accountId, UUID visitId, ExaminationDecisionRequest request) {
        return medicalCheckService.decideExamination(accountId, visitId, request);
    }

    public MedicalCheck findLatestCheckByDonor(UUID donorId) {
        return medicalCheckService.findLatestByDonor(donorId);
    }

    // === Lab examination operations (delegated) ===

    @Transactional
    public LabExaminationRequest createLabRequest(UUID accountId, UUID visitId) {
        return labExaminationService.createLabRequest(accountId, visitId);
    }

    // === Donation operations (delegated) ===

    @Transactional
    public Donation recordDonation(UUID accountId, DonationRequest request) {
        return donationService.recordDonation(accountId, request);
    }

    @Transactional
    public Donation publishDonation(UUID accountId, UUID donationId) {
        return donationService.publishDonation(accountId, donationId);
    }

    // === Sample operations (delegated) ===

    @Transactional
    public Sample registerSample(SampleRequest request) {
        return sampleService.registerSample(request);
    }

    // === Adverse reaction operations (delegated) ===

    @Transactional
    public AdverseReaction registerReaction(UUID accountId, AdverseReactionRequest request) {
        return donationService.registerAdverseReaction(accountId, request);
    }

    // === Donor status operations ===

    @Transactional
    public DonorProfile updateDonorStatus(UUID donorId, String donorStatus) {
        DonorProfile donor = donorProfileRepository.findById(donorId)
                .orElseThrow(() -> NotFoundException.entity("Donor", donorId));

        donor.setDonorStatus(DonorStatus.valueOf(donorStatus.trim().toUpperCase()));
        return donorProfileRepository.save(donor);
    }

    // === Legacy support types ===

    /**
     * @deprecated Use {@link MedicalCheckService.MedicalCheckResult} directly
     */
    @Deprecated(forRemoval = true)
    public record MedicalCheckResult(MedicalCheck check, Deferral deferral) {
        public static MedicalCheckResult from(MedicalCheckService.MedicalCheckResult result) {
            return new MedicalCheckResult(result.check(), result.deferral());
        }
    }
}

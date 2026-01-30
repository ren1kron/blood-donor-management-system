package ifmo.se.coursach_back.medical.api;

import ifmo.se.coursach_back.medical.api.dto.AdverseReactionRequest;
import ifmo.se.coursach_back.medical.api.dto.AdverseReactionResponse;
import ifmo.se.coursach_back.medical.api.dto.DonationRequest;
import ifmo.se.coursach_back.medical.api.dto.DonationResponse;
import ifmo.se.coursach_back.medical.api.dto.ExaminationDecisionRequest;
import ifmo.se.coursach_back.medical.api.dto.ExaminationQueueResponse;
import ifmo.se.coursach_back.medical.api.dto.MedicalCheckRequest;
import ifmo.se.coursach_back.medical.api.dto.MedicalCheckResponse;
import ifmo.se.coursach_back.medical.api.dto.PendingExaminationResponse;
import ifmo.se.coursach_back.medical.api.dto.ReviewExaminationRequest;
import ifmo.se.coursach_back.medical.api.dto.SampleRequest;
import ifmo.se.coursach_back.medical.api.dto.SampleResponse;
import ifmo.se.coursach_back.medical.api.dto.ScheduledDonorResponse;
import ifmo.se.coursach_back.medical.api.dto.UpdateDonorStatusRequest;
import ifmo.se.coursach_back.medical.application.MedicalWorkflowService;
import ifmo.se.coursach_back.medical.application.command.PublishDonationCommand;
import ifmo.se.coursach_back.medical.application.command.RecordDonationCommand;
import ifmo.se.coursach_back.medical.application.command.RecordMedicalCheckCommand;
import ifmo.se.coursach_back.medical.application.command.RegisterReactionCommand;
import ifmo.se.coursach_back.medical.application.command.RegisterSampleCommand;
import ifmo.se.coursach_back.medical.application.command.ReviewExaminationCommand;
import ifmo.se.coursach_back.medical.application.command.UpdateDonorStatusCommand;
import ifmo.se.coursach_back.medical.application.result.DonationResult;
import ifmo.se.coursach_back.medical.application.result.ExaminationReviewResult;
import ifmo.se.coursach_back.medical.application.result.MedicalCheckResult;
import ifmo.se.coursach_back.medical.application.result.PendingExaminationResult;
import ifmo.se.coursach_back.medical.application.result.ReactionResult;
import ifmo.se.coursach_back.medical.application.result.SampleResult;
import ifmo.se.coursach_back.medical.application.result.ScheduledDonorResult;
import ifmo.se.coursach_back.medical.application.usecase.ListMedicalQueueUseCase;
import ifmo.se.coursach_back.medical.application.usecase.ListPendingExaminationsUseCase;
import ifmo.se.coursach_back.medical.application.usecase.PublishDonationUseCase;
import ifmo.se.coursach_back.medical.application.usecase.RecordDonationUseCase;
import ifmo.se.coursach_back.medical.application.usecase.RecordMedicalCheckUseCase;
import ifmo.se.coursach_back.medical.application.usecase.RegisterAdverseReactionUseCase;
import ifmo.se.coursach_back.medical.application.usecase.RegisterSampleUseCase;
import ifmo.se.coursach_back.medical.application.usecase.ReviewExaminationUseCase;
import ifmo.se.coursach_back.medical.application.usecase.UpdateDonorStatusUseCase;
import ifmo.se.coursach_back.appointment.domain.Booking;
import ifmo.se.coursach_back.nurse.domain.CollectionSession;
import ifmo.se.coursach_back.medical.domain.Deferral;
import ifmo.se.coursach_back.medical.domain.Donation;
import ifmo.se.coursach_back.lab.domain.LabExaminationRequest;
import ifmo.se.coursach_back.medical.domain.MedicalCheck;
import ifmo.se.coursach_back.medical.domain.Sample;
import ifmo.se.coursach_back.appointment.domain.SlotPurpose;
import ifmo.se.coursach_back.appointment.domain.Visit;
import ifmo.se.coursach_back.security.AccountPrincipal;
import jakarta.validation.Valid;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/medical")
@RequiredArgsConstructor
@PreAuthorize("hasRole('DOCTOR')")
public class MedicalWorkflowController {
    private final ListMedicalQueueUseCase listMedicalQueueUseCase;
    private final RecordMedicalCheckUseCase recordMedicalCheckUseCase;
    private final RecordDonationUseCase recordDonationUseCase;
    private final PublishDonationUseCase publishDonationUseCase;
    private final RegisterSampleUseCase registerSampleUseCase;
    private final RegisterAdverseReactionUseCase registerAdverseReactionUseCase;
    private final UpdateDonorStatusUseCase updateDonorStatusUseCase;
    private final ListPendingExaminationsUseCase listPendingExaminationsUseCase;
    private final ReviewExaminationUseCase reviewExaminationUseCase;
    // Keep MedicalWorkflowService for operations not yet migrated to use cases
    private final MedicalWorkflowService medicalWorkflowService;

    @GetMapping("/queue")
    public List<ScheduledDonorResponse> listQueue(@RequestParam(value = "from", required = false)
                                                  OffsetDateTime from) {
        OffsetDateTime start = from == null ? OffsetDateTime.now().minusHours(2) : from;
        List<ScheduledDonorResult> results = listMedicalQueueUseCase.execute(start);
        return results.stream()
                .map(r -> new ScheduledDonorResponse(
                        r.bookingId(), r.visitId(), r.donorId(), r.donorFullName(),
                        r.donorStatus() != null ? ifmo.se.coursach_back.donor.domain.DonorStatus.valueOf(r.donorStatus()) : null,
                        r.slotId(),
                        r.slotPurpose() != null ? ifmo.se.coursach_back.appointment.domain.SlotPurpose.valueOf(r.slotPurpose()) : null,
                        r.slotStartAt(), r.slotEndAt(), r.slotLocation(),
                        r.bookingStatus() != null ? ifmo.se.coursach_back.appointment.domain.BookingStatus.valueOf(r.bookingStatus()) : null,
                        r.medicalCheckDecision() != null ? 
                            ifmo.se.coursach_back.medical.domain.MedicalCheckDecision.valueOf(r.medicalCheckDecision()) : null,
                        r.hasDonation(), r.canDonate(), r.donationId(), r.donationPublished(),
                        r.collectionSessionId(), r.collectionSessionStatus() != null ?
                            ifmo.se.coursach_back.nurse.domain.CollectionSessionStatus.valueOf(r.collectionSessionStatus()) : null,
                        r.collectionSessionStartedAt(), r.collectionSessionEndedAt(), r.collectionSessionNurseName(),
                        null, null, null, null, null
                ))
                .toList();
    }

    @PostMapping("/checks")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<MedicalCheckResponse> recordCheck(@AuthenticationPrincipal AccountPrincipal principal,
                                                            @Valid @RequestBody MedicalCheckRequest request) {
        RecordMedicalCheckCommand command = new RecordMedicalCheckCommand(
                principal.getId(), request.bookingId(), request.visitId(),
                request.weightKg(), request.hemoglobinGl(),
                request.systolicMmhg(), request.diastolicMmhg(), null, null,
                request.decision(),
                request.deferral() != null ? new RecordMedicalCheckCommand.DeferralInfo(
                        request.deferral().deferralType(),
                        request.deferral().reason(),
                        request.deferral().endsAt()
                ) : null
        );
        MedicalCheckResult result = recordMedicalCheckUseCase.execute(command);
        MedicalCheckResponse response = new MedicalCheckResponse(
                result.checkId(), null, null, null,
                ifmo.se.coursach_back.medical.domain.MedicalCheckDecision.valueOf(result.decision()),
                result.decisionAt(),
                result.deferralInfo() != null ? result.deferralInfo().deferralId() : null
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/donations")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<DonationResponse> registerDonation(@AuthenticationPrincipal AccountPrincipal principal,
                                                             @Valid @RequestBody DonationRequest request) {
        RecordDonationCommand command = new RecordDonationCommand(
                principal.getId(), request.bookingId(), request.visitId(),
                request.donationType(), request.volumeMl(), request.performedAt(), null
        );
        DonationResult result = recordDonationUseCase.execute(command);
        DonationResponse response = new DonationResponse(
                result.donationId(), result.visitId(), null, null,
                result.donationType(), result.volumeMl(),
                result.performedAt(), result.published(), result.publishedAt()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/donations/{donationId}/publish")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<DonationResponse> publishDonation(@AuthenticationPrincipal AccountPrincipal principal,
                                                            @PathVariable UUID donationId) {
        PublishDonationCommand command = new PublishDonationCommand(principal.getId(), donationId);
        DonationResult result = publishDonationUseCase.execute(command);
        DonationResponse response = new DonationResponse(
                result.donationId(), result.visitId(), null, null,
                result.donationType(), result.volumeMl(),
                result.performedAt(), result.published(), result.publishedAt()
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping("/samples")
    public ResponseEntity<SampleResponse> registerSample(@Valid @RequestBody SampleRequest request) {
        RegisterSampleCommand command = new RegisterSampleCommand(
                request.donationId(), request.sampleCode(),
                request.status(), request.quarantineReason(), request.rejectionReason()
        );
        SampleResult result = registerSampleUseCase.execute(command);
        SampleResponse response = new SampleResponse(
                result.sampleId(), result.donationId(), result.sampleCode(),
                ifmo.se.coursach_back.medical.domain.SampleStatus.valueOf(result.status()),
                result.collectedAt()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/reactions")
    public ResponseEntity<AdverseReactionResponse> registerReaction(
            @AuthenticationPrincipal AccountPrincipal principal,
            @Valid @RequestBody AdverseReactionRequest request) {
        RegisterReactionCommand command = new RegisterReactionCommand(
                principal.getId(), request.donationId(), request.occurredAt(),
                request.severity(), request.description()
        );
        ReactionResult result = registerAdverseReactionUseCase.execute(command);
        AdverseReactionResponse response = new AdverseReactionResponse(
                result.reactionId(), result.donationId(), result.occurredAt(),
                result.severity(), result.description()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/donors/{donorId}/status")
    public ResponseEntity<Void> updateDonorStatus(@PathVariable UUID donorId,
                                                  @Valid @RequestBody UpdateDonorStatusRequest request) {
        UpdateDonorStatusCommand command = new UpdateDonorStatusCommand(donorId, request.donorStatus());
        updateDonorStatusUseCase.execute(command);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/examinations/pending")
    @PreAuthorize("hasRole('DOCTOR')")
    public List<PendingExaminationResponse> listPendingExaminations() {
        List<PendingExaminationResult> results = listPendingExaminationsUseCase.execute();
        return results.stream()
                .map(r -> new PendingExaminationResponse(
                        r.checkId(), r.visitId(), null, r.donorFullName(),
                        null, null, null, r.hemoglobinGl(), null, null, null, null,
                        null, null, r.submittedAt()
                ))
                .toList();
    }

    @GetMapping("/examinations/queue")
    @PreAuthorize("hasRole('DOCTOR')")
    public List<ExaminationQueueResponse> listExaminationQueue(
            @RequestParam(value = "from", required = false) OffsetDateTime from) {
        OffsetDateTime start = from == null ? OffsetDateTime.now().minusHours(8) : from;
        List<Booking> bookings = medicalWorkflowService.listConfirmedExaminationBookings(start);
        List<UUID> bookingIds = bookings.stream().map(Booking::getId).toList();
        Map<UUID, Visit> visitsByBooking = medicalWorkflowService.loadVisitsByBookingIds(bookingIds);
        List<UUID> visitIds = visitsByBooking.values().stream().map(Visit::getId).toList();
        Map<UUID, LabExaminationRequest> requestsByVisit = medicalWorkflowService.loadLabRequestsByVisitIds(visitIds);
        Map<UUID, MedicalCheck> checksByVisit = medicalWorkflowService.loadMedicalChecksByVisitIds(visitIds);

        return bookings.stream()
                .map(booking -> {
                    Visit visit = visitsByBooking.get(booking.getId());
                    if (visit == null) {
                        return null;
                    }
                    LabExaminationRequest request = requestsByVisit.get(visit.getId());
                    MedicalCheck check = checksByVisit.get(visit.getId());
                    return ExaminationQueueResponse.from(visit, request, check);
                })
                .filter(java.util.Objects::nonNull)
                .toList();
    }

    @PostMapping("/examinations/{visitId}/lab-request")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<UUID> createLabRequest(@AuthenticationPrincipal AccountPrincipal principal,
                                                 @PathVariable UUID visitId) {
        LabExaminationRequest request = medicalWorkflowService.createLabRequest(principal.getId(), visitId);
        return ResponseEntity.status(HttpStatus.CREATED).body(request.getId());
    }

    @PostMapping("/examinations/{visitId}/decision")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<MedicalCheckResponse> decideExamination(
            @AuthenticationPrincipal AccountPrincipal principal,
            @PathVariable UUID visitId,
            @Valid @RequestBody ExaminationDecisionRequest request) {
        MedicalWorkflowService.MedicalCheckResult result =
                medicalWorkflowService.decideExamination(principal.getId(), visitId, request);
        Booking booking = result.check().getVisit().getBooking();
        Deferral deferral = result.deferral();
        MedicalCheckResponse response = MedicalCheckResponse.from(result.check(), booking, deferral);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/examinations/review")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<MedicalCheckResponse> reviewExamination(
            @AuthenticationPrincipal AccountPrincipal principal,
            @Valid @RequestBody ReviewExaminationRequest request) {
        ReviewExaminationCommand command = new ReviewExaminationCommand(
                principal.getId(), request.examinationId(), request.decision(),
                request.deferral() != null ? new ReviewExaminationCommand.DeferralInfo(
                        request.deferral().deferralType(),
                        request.deferral().reason(),
                        request.deferral().endsAt()
                ) : null
        );
        ExaminationReviewResult result = reviewExaminationUseCase.execute(command);
        MedicalCheckResponse response = new MedicalCheckResponse(
                result.checkId(), null, null, null,
                ifmo.se.coursach_back.medical.domain.MedicalCheckDecision.valueOf(result.decision()),
                result.decisionAt(),
                result.deferralInfo() != null ? result.deferralInfo().deferralId() : null
        );
        return ResponseEntity.ok(response);
    }
}

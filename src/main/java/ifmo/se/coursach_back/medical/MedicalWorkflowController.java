package ifmo.se.coursach_back.medical;

import ifmo.se.coursach_back.medical.dto.AdverseReactionRequest;
import ifmo.se.coursach_back.medical.dto.AdverseReactionResponse;
import ifmo.se.coursach_back.medical.dto.DonationRequest;
import ifmo.se.coursach_back.medical.dto.DonationResponse;
import ifmo.se.coursach_back.medical.dto.ExaminationDecisionRequest;
import ifmo.se.coursach_back.medical.dto.ExaminationQueueResponse;
import ifmo.se.coursach_back.medical.dto.MedicalCheckRequest;
import ifmo.se.coursach_back.medical.dto.MedicalCheckResponse;
import ifmo.se.coursach_back.medical.dto.PendingExaminationResponse;
import ifmo.se.coursach_back.medical.dto.ReviewExaminationRequest;
import ifmo.se.coursach_back.medical.dto.SampleRequest;
import ifmo.se.coursach_back.medical.dto.SampleResponse;
import ifmo.se.coursach_back.medical.dto.ScheduledDonorResponse;
import ifmo.se.coursach_back.medical.dto.UpdateDonorStatusRequest;
import ifmo.se.coursach_back.model.Booking;
import ifmo.se.coursach_back.model.Deferral;
import ifmo.se.coursach_back.model.Donation;
import ifmo.se.coursach_back.model.LabExaminationRequest;
import ifmo.se.coursach_back.model.MedicalCheck;
import ifmo.se.coursach_back.model.Sample;
import ifmo.se.coursach_back.model.Visit;
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
@PreAuthorize("hasAnyRole('DOCTOR', 'NURSE')")
public class MedicalWorkflowController {
    private final MedicalWorkflowService medicalWorkflowService;

    @GetMapping("/queue")
    public List<ScheduledDonorResponse> listQueue(@RequestParam(value = "from", required = false)
                                                  OffsetDateTime from) {
        OffsetDateTime start = from == null ? OffsetDateTime.now().minusHours(2) : from;
        List<Booking> bookings = medicalWorkflowService.listScheduledBookings(start);
        List<UUID> bookingIds = bookings.stream().map(Booking::getId).toList();
        Map<UUID, Visit> visitsByBooking = medicalWorkflowService.loadVisitsByBookingIds(bookingIds);
        
        // Load medical checks and donations for visits
        List<UUID> visitIds = visitsByBooking.values().stream().map(Visit::getId).toList();
        Map<UUID, MedicalCheck> checksByVisit = medicalWorkflowService.loadMedicalChecksByVisitIds(visitIds);
        Map<UUID, Donation> donationsByVisit = medicalWorkflowService.loadDonationsByVisitIds(visitIds);

        return bookings.stream()
                .map(booking -> {
                    Visit visit = visitsByBooking.get(booking.getId());
                    MedicalCheck check = visit != null ? checksByVisit.get(visit.getId()) : null;
                    Donation donation = visit != null ? donationsByVisit.get(visit.getId()) : null;
                    return ScheduledDonorResponse.from(booking, visit, check, donation);
                })
                .toList();
    }

    @PostMapping("/checks")
    public ResponseEntity<MedicalCheckResponse> recordCheck(@AuthenticationPrincipal AccountPrincipal principal,
                                                            @Valid @RequestBody MedicalCheckRequest request) {
        MedicalWorkflowService.MedicalCheckResult result =
                medicalWorkflowService.recordMedicalCheck(principal.getId(), request);
        Booking booking = result.check().getVisit().getBooking();
        Deferral deferral = result.deferral();
        MedicalCheckResponse response = MedicalCheckResponse.from(result.check(), booking, deferral);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/donations")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<DonationResponse> registerDonation(@AuthenticationPrincipal AccountPrincipal principal,
                                                             @Valid @RequestBody DonationRequest request) {
        Donation donation = medicalWorkflowService.recordDonation(principal.getId(), request);
        Booking booking = donation.getVisit().getBooking();
        return ResponseEntity.status(HttpStatus.CREATED).body(DonationResponse.from(donation, booking));
    }

    @PostMapping("/donations/{donationId}/publish")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<DonationResponse> publishDonation(@AuthenticationPrincipal AccountPrincipal principal,
                                                            @PathVariable UUID donationId) {
        Donation donation = medicalWorkflowService.publishDonation(principal.getId(), donationId);
        Booking booking = donation.getVisit().getBooking();
        return ResponseEntity.ok(DonationResponse.from(donation, booking));
    }

    @PostMapping("/samples")
    public ResponseEntity<SampleResponse> registerSample(@Valid @RequestBody SampleRequest request) {
        Sample sample = medicalWorkflowService.registerSample(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(SampleResponse.from(sample));
    }

    @PostMapping("/reactions")
    public ResponseEntity<AdverseReactionResponse> registerReaction(
            @AuthenticationPrincipal AccountPrincipal principal,
            @Valid @RequestBody AdverseReactionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(AdverseReactionResponse.from(medicalWorkflowService.registerReaction(principal.getId(), request)));
    }

    @PatchMapping("/donors/{donorId}/status")
    public ResponseEntity<Void> updateDonorStatus(@PathVariable UUID donorId,
                                                  @Valid @RequestBody UpdateDonorStatusRequest request) {
        medicalWorkflowService.updateDonorStatus(donorId, request.donorStatus());
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/examinations/pending")
    @PreAuthorize("hasRole('DOCTOR')")
    public List<PendingExaminationResponse> listPendingExaminations() {
        return medicalWorkflowService.listPendingExaminations().stream()
                .map(PendingExaminationResponse::from)
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
        MedicalWorkflowService.MedicalCheckResult result =
                medicalWorkflowService.reviewExamination(principal.getId(), request);
        Booking booking = result.check().getVisit().getBooking();
        Deferral deferral = result.deferral();
        MedicalCheckResponse response = MedicalCheckResponse.from(result.check(), booking, deferral);
        return ResponseEntity.ok(response);
    }
}

package ifmo.se.coursach_back.medical;

import ifmo.se.coursach_back.medical.dto.AdverseReactionRequest;
import ifmo.se.coursach_back.medical.dto.AdverseReactionResponse;
import ifmo.se.coursach_back.medical.dto.DonationRequest;
import ifmo.se.coursach_back.medical.dto.DonationResponse;
import ifmo.se.coursach_back.medical.dto.MedicalCheckRequest;
import ifmo.se.coursach_back.medical.dto.MedicalCheckResponse;
import ifmo.se.coursach_back.medical.dto.SampleRequest;
import ifmo.se.coursach_back.medical.dto.SampleResponse;
import ifmo.se.coursach_back.medical.dto.ScheduledDonorResponse;
import ifmo.se.coursach_back.medical.dto.UpdateDonorStatusRequest;
import ifmo.se.coursach_back.model.Booking;
import ifmo.se.coursach_back.model.Deferral;
import ifmo.se.coursach_back.model.Donation;
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
        OffsetDateTime start = from == null ? OffsetDateTime.now() : from;
        List<Booking> bookings = medicalWorkflowService.listScheduledBookings(start);
        List<UUID> bookingIds = bookings.stream().map(Booking::getId).toList();
        Map<UUID, Visit> visitsByBooking = medicalWorkflowService.loadVisitsByBookingIds(bookingIds);

        return bookings.stream()
                .map(booking -> ScheduledDonorResponse.from(booking, visitsByBooking.get(booking.getId())))
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
    public ResponseEntity<DonationResponse> registerDonation(@AuthenticationPrincipal AccountPrincipal principal,
                                                             @Valid @RequestBody DonationRequest request) {
        Donation donation = medicalWorkflowService.recordDonation(principal.getId(), request);
        Booking booking = donation.getVisit().getBooking();
        return ResponseEntity.status(HttpStatus.CREATED).body(DonationResponse.from(donation, booking));
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
}

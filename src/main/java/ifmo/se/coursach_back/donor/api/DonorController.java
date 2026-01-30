package ifmo.se.coursach_back.donor.api;

import ifmo.se.coursach_back.donor.api.dto.ConsentRequest;
import ifmo.se.coursach_back.donor.api.dto.ConsentResponse;
import ifmo.se.coursach_back.donor.api.dto.DeferralStatusResponse;
import ifmo.se.coursach_back.donor.api.dto.DonationHistoryResponse;
import ifmo.se.coursach_back.donor.api.dto.DonorNotificationResponse;
import ifmo.se.coursach_back.donor.api.dto.DonorProfileResponse;
import ifmo.se.coursach_back.donor.api.dto.EligibilityResponse;
import ifmo.se.coursach_back.donor.api.dto.LabResultResponse;
import ifmo.se.coursach_back.donor.api.dto.UpdateDonorProfileRequest;
import ifmo.se.coursach_back.donor.api.dto.VisitHistoryResponse;
import ifmo.se.coursach_back.donor.application.DonorService;
import ifmo.se.coursach_back.donor.application.command.SubmitConsentCommand;
import ifmo.se.coursach_back.donor.application.command.UpdateDonorProfileCommand;
import ifmo.se.coursach_back.donor.application.result.ConsentResult;
import ifmo.se.coursach_back.donor.application.result.DeferralStatusResult;
import ifmo.se.coursach_back.donor.application.result.DonationHistoryResult;
import ifmo.se.coursach_back.donor.application.result.DonorProfileResult;
import ifmo.se.coursach_back.donor.application.result.EligibilityResult;
import ifmo.se.coursach_back.donor.application.result.NotificationResult;
import ifmo.se.coursach_back.donor.application.usecase.AcknowledgeNotificationUseCase;
import ifmo.se.coursach_back.donor.application.usecase.CheckEligibilityUseCase;
import ifmo.se.coursach_back.donor.application.usecase.GetDonorProfileUseCase;
import ifmo.se.coursach_back.donor.application.usecase.ListDonationHistoryUseCase;
import ifmo.se.coursach_back.donor.application.usecase.ListDonorNotificationsUseCase;
import ifmo.se.coursach_back.donor.application.usecase.SubmitConsentUseCase;
import ifmo.se.coursach_back.donor.application.usecase.UpdateDonorProfileUseCase;
import ifmo.se.coursach_back.security.AccountPrincipal;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/donor")
@RequiredArgsConstructor
@PreAuthorize("hasRole('DONOR')")
public class DonorController {
    private final GetDonorProfileUseCase getDonorProfileUseCase;
    private final UpdateDonorProfileUseCase updateDonorProfileUseCase;
    private final SubmitConsentUseCase submitConsentUseCase;
    private final ListDonationHistoryUseCase listDonationHistoryUseCase;
    private final CheckEligibilityUseCase checkEligibilityUseCase;
    private final ListDonorNotificationsUseCase listDonorNotificationsUseCase;
    private final AcknowledgeNotificationUseCase acknowledgeNotificationUseCase;
    // Keep DonorService for operations not yet migrated to use cases
    private final DonorService donorService;

    @GetMapping("/profile")
    public DonorProfileResponse getProfile(@AuthenticationPrincipal AccountPrincipal principal) {
        DonorProfileResult result = getDonorProfileUseCase.execute(principal.getId());
        return new DonorProfileResponse(
                result.accountId(), result.donorId(), result.fullName(),
                result.birthDate(), result.bloodGroup(), result.rhFactor(),
                result.donorStatus(), result.email(), result.phone()
        );
    }

    @PutMapping("/profile")
    public DonorProfileResponse updateProfile(@AuthenticationPrincipal AccountPrincipal principal,
                                              @Valid @RequestBody UpdateDonorProfileRequest request) {
        UpdateDonorProfileCommand command = new UpdateDonorProfileCommand(
                principal.getId(), request.fullName(), request.birthDate(),
                request.bloodGroup(), request.rhFactor(), request.email(), request.phone()
        );
        DonorProfileResult result = updateDonorProfileUseCase.execute(command);
        return new DonorProfileResponse(
                result.accountId(), result.donorId(), result.fullName(),
                result.birthDate(), result.bloodGroup(), result.rhFactor(),
                result.donorStatus(), result.email(), result.phone()
        );
    }

    @PostMapping("/consents")
    public ResponseEntity<ConsentResponse> submitConsent(@AuthenticationPrincipal AccountPrincipal principal,
                                                         @Valid @RequestBody ConsentRequest request) {
        SubmitConsentCommand command = new SubmitConsentCommand(
                principal.getId(), request.visitId(), request.bookingId(), request.consentType()
        );
        ConsentResult result = submitConsentUseCase.execute(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ConsentResponse(
                result.id(), result.visitId(), result.donorId(), result.consentType(), result.givenAt()
        ));
    }

    @GetMapping("/donations")
    public List<DonationHistoryResponse> donationHistory(@AuthenticationPrincipal AccountPrincipal principal) {
        List<DonationHistoryResult> results = listDonationHistoryUseCase.execute(principal.getId());
        return results.stream()
                .map(r -> new DonationHistoryResponse(
                        r.donationId(), r.visitId(), r.performedAt(), r.donationType(),
                        r.volumeMl(), r.publishedAt(), r.preVitalsJson(), r.postVitalsJson(), r.hasVitals()
                ))
                .toList();
    }

    @GetMapping("/test-results")
    public List<LabResultResponse> testResults(@AuthenticationPrincipal AccountPrincipal principal) {
        return donorService.listPublishedResults(principal.getId()).stream()
                .map(LabResultResponse::from)
                .toList();
    }

    @GetMapping("/visits")
    public List<VisitHistoryResponse> visitHistory(@AuthenticationPrincipal AccountPrincipal principal) {
        return donorService.listVisitHistory(principal.getId()).stream()
                .map(check -> VisitHistoryResponse.from(check, check.getVisit() != null))
                .toList();
    }

    @GetMapping("/eligibility")
    public EligibilityResponse eligibility(@AuthenticationPrincipal AccountPrincipal principal) {
        EligibilityResult result = checkEligibilityUseCase.execute(principal.getId());
        DeferralStatusResponse deferral = null;
        if (result.activeDeferral() != null) {
            DeferralStatusResult d = result.activeDeferral();
            deferral = new DeferralStatusResponse(d.deferralId(), d.deferralType(), d.reason(), d.startsAt(), d.endsAt());
        }
        return new EligibilityResponse(
                result.donorStatus(), result.eligible(), result.canBookDonation(),
                result.lastDonationAt(), result.nextEligibleAt(), result.medicalCheckValidUntil(), deferral
        );
    }

    @GetMapping("/notifications")
    public List<DonorNotificationResponse> notifications(@AuthenticationPrincipal AccountPrincipal principal) {
        List<NotificationResult> results = listDonorNotificationsUseCase.execute(principal.getId());
        return results.stream()
                .map(r -> new DonorNotificationResponse(
                        r.deliveryId(), r.notificationId(), r.topic(), r.body(),
                        r.channel(), r.createdAt(), r.sentAt(), r.status()
                ))
                .toList();
    }

    @PostMapping("/notifications/{deliveryId}/ack")
    public ResponseEntity<Void> acknowledge(@AuthenticationPrincipal AccountPrincipal principal,
                                            @PathVariable UUID deliveryId) {
        acknowledgeNotificationUseCase.execute(principal.getId(), deliveryId);
        return ResponseEntity.noContent().build();
    }
}

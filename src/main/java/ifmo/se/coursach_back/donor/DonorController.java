package ifmo.se.coursach_back.donor;

import ifmo.se.coursach_back.donor.dto.ConsentRequest;
import ifmo.se.coursach_back.donor.dto.ConsentResponse;
import ifmo.se.coursach_back.donor.dto.DonationHistoryResponse;
import ifmo.se.coursach_back.donor.dto.DonorNotificationResponse;
import ifmo.se.coursach_back.donor.dto.DonorProfileResponse;
import ifmo.se.coursach_back.donor.dto.EligibilityResponse;
import ifmo.se.coursach_back.donor.dto.LabResultResponse;
import ifmo.se.coursach_back.donor.dto.UpdateDonorProfileRequest;
import ifmo.se.coursach_back.donor.dto.VisitHistoryResponse;
import ifmo.se.coursach_back.model.Consent;
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
    private final DonorService donorService;

    @GetMapping("/profile")
    public DonorProfileResponse getProfile(@AuthenticationPrincipal AccountPrincipal principal) {
        return donorService.getProfile(principal.getId());
    }

    @PutMapping("/profile")
    public DonorProfileResponse updateProfile(@AuthenticationPrincipal AccountPrincipal principal,
                                              @Valid @RequestBody UpdateDonorProfileRequest request) {
        return donorService.updateProfile(principal.getId(), request);
    }

    @PostMapping("/consents")
    public ResponseEntity<ConsentResponse> submitConsent(@AuthenticationPrincipal AccountPrincipal principal,
                                                         @Valid @RequestBody ConsentRequest request) {
        Consent consent = donorService.createConsent(principal.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ConsentResponse.from(consent));
    }

    @GetMapping("/donations")
    public List<DonationHistoryResponse> donationHistory(@AuthenticationPrincipal AccountPrincipal principal) {
        return donorService.listDonationHistory(principal.getId());
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
        return donorService.getEligibility(principal.getId());
    }

    @GetMapping("/notifications")
    public List<DonorNotificationResponse> notifications(@AuthenticationPrincipal AccountPrincipal principal) {
        return donorService.listNotifications(principal.getId()).stream()
                .map(DonorNotificationResponse::from)
                .toList();
    }

    @PostMapping("/notifications/{deliveryId}/ack")
    public ResponseEntity<Void> acknowledge(@AuthenticationPrincipal AccountPrincipal principal,
                                            @PathVariable UUID deliveryId) {
        donorService.acknowledgeNotification(principal.getId(), deliveryId);
        return ResponseEntity.noContent().build();
    }
}

package ifmo.se.coursach_back.admin;

import ifmo.se.coursach_back.admin.dto.AdminRegisterDonorRequest;
import ifmo.se.coursach_back.admin.dto.AdminRegisterDonorResponse;
import ifmo.se.coursach_back.admin.dto.EligibleDonorResponse;
import ifmo.se.coursach_back.admin.dto.ExpiredDocumentResponse;
import ifmo.se.coursach_back.admin.dto.MarkNotifiedRequest;
import ifmo.se.coursach_back.admin.dto.NotificationMarkResponse;
import ifmo.se.coursach_back.security.AccountPrincipal;
import jakarta.validation.Valid;
import java.time.LocalDate;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    private final AdminService adminService;

    @PostMapping("/donors/phone-registration")
    public ResponseEntity<AdminRegisterDonorResponse> registerDonor(@Valid @RequestBody AdminRegisterDonorRequest request) {
        AdminRegisterDonorResponse response = adminService.registerDonorByPhone(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/reminders/eligible")
    public List<EligibleDonorResponse> listEligible(@RequestParam(value = "minDaysSinceDonation", defaultValue = "56")
                                                     int minDaysSinceDonation) {
        return adminService.listEligibleDonors(minDaysSinceDonation);
    }

    @PostMapping("/reminders/eligible/{donorId}/mark-notified")
    public ResponseEntity<NotificationMarkResponse> markEligibleNotified(
            @AuthenticationPrincipal AccountPrincipal principal,
            @PathVariable UUID donorId,
            @RequestBody(required = false) MarkNotifiedRequest request) {
        NotificationMarkResponse response = adminService.markDonorRevisitNotified(principal.getId(), donorId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/documents/expired")
    public List<ExpiredDocumentResponse> listExpiredDocs(@RequestParam(value = "asOf", required = false) LocalDate asOf) {
        return adminService.listExpiredDocuments(asOf);
    }

    @PostMapping("/documents/expired/{documentId}/mark-notified")
    public ResponseEntity<NotificationMarkResponse> markExpiredNotified(
            @AuthenticationPrincipal AccountPrincipal principal,
            @PathVariable UUID documentId,
            @RequestBody(required = false) MarkNotifiedRequest request) {
        NotificationMarkResponse response = adminService.markExpiredDocumentNotified(principal.getId(), documentId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}

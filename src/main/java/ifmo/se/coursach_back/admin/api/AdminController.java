package ifmo.se.coursach_back.admin.api;

import ifmo.se.coursach_back.admin.api.dto.AdminRegisterDonorRequest;
import ifmo.se.coursach_back.admin.api.dto.AdminRegisterDonorResponse;
import ifmo.se.coursach_back.admin.api.dto.EligibleDonorResponse;
import ifmo.se.coursach_back.admin.api.dto.ExpiredDocumentResponse;
import ifmo.se.coursach_back.admin.api.dto.MarkNotifiedRequest;
import ifmo.se.coursach_back.admin.api.dto.NotificationMarkResponse;
import ifmo.se.coursach_back.admin.api.dto.ReportsSummaryResponse;
import ifmo.se.coursach_back.admin.api.dto.SendReminderRequest;
import ifmo.se.coursach_back.admin.api.dto.SendReminderResponse;
import ifmo.se.coursach_back.admin.application.command.MarkNotifiedCommand;
import ifmo.se.coursach_back.admin.application.command.RegisterDonorByPhoneCommand;
import ifmo.se.coursach_back.admin.application.command.SendReminderCommand;
import ifmo.se.coursach_back.admin.application.result.EligibleDonorResult;
import ifmo.se.coursach_back.admin.application.result.ExpiredDocumentResult;
import ifmo.se.coursach_back.admin.application.result.NotificationMarkResult;
import ifmo.se.coursach_back.admin.application.result.RegisterDonorResult;
import ifmo.se.coursach_back.admin.application.result.ReminderSentResult;
import ifmo.se.coursach_back.admin.application.result.ReportsSummaryResult;
import ifmo.se.coursach_back.admin.application.usecase.GetReportsSummaryUseCase;
import ifmo.se.coursach_back.admin.application.usecase.ListEligibleDonorsUseCase;
import ifmo.se.coursach_back.admin.application.usecase.ListExpiredDocumentsUseCase;
import ifmo.se.coursach_back.admin.application.usecase.MarkDonorRevisitNotifiedUseCase;
import ifmo.se.coursach_back.admin.application.usecase.MarkExpiredDocumentNotifiedUseCase;
import ifmo.se.coursach_back.admin.application.usecase.RegisterDonorByPhoneUseCase;
import ifmo.se.coursach_back.admin.application.usecase.SendReminderUseCase;
import ifmo.se.coursach_back.security.AccountPrincipal;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.time.OffsetDateTime;
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
    private final RegisterDonorByPhoneUseCase registerDonorByPhoneUseCase;
    private final ListEligibleDonorsUseCase listEligibleDonorsUseCase;
    private final MarkDonorRevisitNotifiedUseCase markDonorRevisitNotifiedUseCase;
    private final ListExpiredDocumentsUseCase listExpiredDocumentsUseCase;
    private final MarkExpiredDocumentNotifiedUseCase markExpiredDocumentNotifiedUseCase;
    private final GetReportsSummaryUseCase getReportsSummaryUseCase;
    private final SendReminderUseCase sendReminderUseCase;

    @PostMapping("/donors/phone-registration")
    public ResponseEntity<AdminRegisterDonorResponse> registerDonor(@Valid @RequestBody AdminRegisterDonorRequest request) {
        RegisterDonorByPhoneCommand command = new RegisterDonorByPhoneCommand(
                request.phone(), request.email(), request.password(),
                request.fullName(), request.birthDate(),
                request.bloodGroup(), request.rhFactor()
        );
        RegisterDonorResult result = registerDonorByPhoneUseCase.execute(command);
        AdminRegisterDonorResponse response = new AdminRegisterDonorResponse(
                result.accountId(), result.profileId(), request.password()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/reminders/eligible")
    public List<EligibleDonorResponse> listEligible(@RequestParam(value = "minDaysSinceDonation", defaultValue = "56")
                                                     int minDaysSinceDonation) {
        List<EligibleDonorResult> results = listEligibleDonorsUseCase.execute(minDaysSinceDonation);
        return results.stream()
                .map(r -> new EligibleDonorResponse(
                        r.donorId(), r.fullName(), r.phone(), r.email(),
                        r.lastDonationAt(), r.daysSinceDonation()
                ))
                .toList();
    }

    @PostMapping("/reminders/eligible/{donorId}/mark-notified")
    public ResponseEntity<NotificationMarkResponse> markEligibleNotified(
            @AuthenticationPrincipal AccountPrincipal principal,
            @PathVariable UUID donorId,
            @Valid @RequestBody(required = false) MarkNotifiedRequest request) {
        MarkNotifiedCommand command = new MarkNotifiedCommand(
                principal.getId(), donorId,
                request != null ? request.channel() : null,
                request != null ? request.body() : null
        );
        NotificationMarkResult result = markDonorRevisitNotifiedUseCase.execute(command);
        NotificationMarkResponse response = new NotificationMarkResponse(
                result.notificationId(), result.deliveryId(), result.sentAt()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/documents/expired")
    public List<ExpiredDocumentResponse> listExpiredDocs(@RequestParam(value = "asOf", required = false) LocalDate asOf) {
        List<ExpiredDocumentResult> results = listExpiredDocumentsUseCase.execute(asOf);
        return results.stream()
                .map(r -> new ExpiredDocumentResponse(
                        r.documentId(), r.donorId(), r.fullName(),
                        r.phone(), r.email(), r.docType(), r.expiresAt()
                ))
                .toList();
    }

    @PostMapping("/documents/expired/{documentId}/mark-notified")
    public ResponseEntity<NotificationMarkResponse> markExpiredNotified(
            @AuthenticationPrincipal AccountPrincipal principal,
            @PathVariable UUID documentId,
            @Valid @RequestBody(required = false) MarkNotifiedRequest request) {
        MarkNotifiedCommand command = new MarkNotifiedCommand(
                principal.getId(), documentId,
                request != null ? request.channel() : null,
                request != null ? request.body() : null
        );
        NotificationMarkResult result = markExpiredDocumentNotifiedUseCase.execute(command);
        NotificationMarkResponse response = new NotificationMarkResponse(
                result.notificationId(), result.deliveryId(), result.sentAt()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/reports/summary")
    public ReportsSummaryResponse getReportsSummary(
            @RequestParam(value = "from", required = false) OffsetDateTime from,
            @RequestParam(value = "to", required = false) OffsetDateTime to) {
        ReportsSummaryResult result = getReportsSummaryUseCase.execute(from, to);
        return new ReportsSummaryResponse(
                result.donorsTotalCount(), result.donorsActiveCount(),
                result.donationsCount(), result.donationsLastWeek(),
                result.donationsLastMonth(), result.samplesCount(),
                result.publishedResultsCount(), result.eligibleCandidatesCount(),
                result.pendingReviewCount(), result.labQueueCount(),
                result.bloodUnitsByGroupRh()
        );
    }

    @PostMapping("/reminders/send")
    public ResponseEntity<SendReminderResponse> sendReminder(
            @AuthenticationPrincipal AccountPrincipal principal,
            @Valid @RequestBody SendReminderRequest request) {
        SendReminderCommand command = new SendReminderCommand(
                principal.getId(), request.donorId(),
                request.topic(), request.body(), request.channel()
        );
        ReminderSentResult result = sendReminderUseCase.execute(command);
        SendReminderResponse response = new SendReminderResponse(
                result.notificationId(), result.deliveryId(),
                result.status(), result.sentAt()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}

package ifmo.se.coursach_back.nurse.api;

import ifmo.se.coursach_back.medical.api.dto.ScheduledDonorResponse;
import ifmo.se.coursach_back.nurse.api.dto.CollectionSessionCreateRequest;
import ifmo.se.coursach_back.nurse.api.dto.CollectionSessionResponse;
import ifmo.se.coursach_back.nurse.api.dto.CollectionSessionUpdateRequest;
import ifmo.se.coursach_back.nurse.application.command.AbortCollectionSessionCommand;
import ifmo.se.coursach_back.nurse.application.command.CompleteCollectionSessionCommand;
import ifmo.se.coursach_back.nurse.application.command.CreateCollectionSessionCommand;
import ifmo.se.coursach_back.nurse.application.command.StartCollectionSessionCommand;
import ifmo.se.coursach_back.nurse.application.result.CollectionSessionResult;
import ifmo.se.coursach_back.nurse.application.result.DonationQueueResult;
import ifmo.se.coursach_back.nurse.application.usecase.AbortCollectionSessionUseCase;
import ifmo.se.coursach_back.nurse.application.usecase.CompleteCollectionSessionUseCase;
import ifmo.se.coursach_back.nurse.application.usecase.CreateCollectionSessionUseCase;
import ifmo.se.coursach_back.nurse.application.usecase.GetCollectionSessionUseCase;
import ifmo.se.coursach_back.nurse.application.usecase.ListDonationQueueUseCase;
import ifmo.se.coursach_back.nurse.application.usecase.StartCollectionSessionUseCase;
import ifmo.se.coursach_back.security.AccountPrincipal;
import jakarta.validation.Valid;
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
@RequestMapping("/api/nurse")
@RequiredArgsConstructor
@PreAuthorize("hasRole('NURSE')")
public class NurseController {
    private final ListDonationQueueUseCase listDonationQueueUseCase;
    private final CreateCollectionSessionUseCase createCollectionSessionUseCase;
    private final StartCollectionSessionUseCase startCollectionSessionUseCase;
    private final CompleteCollectionSessionUseCase completeCollectionSessionUseCase;
    private final AbortCollectionSessionUseCase abortCollectionSessionUseCase;
    private final GetCollectionSessionUseCase getCollectionSessionUseCase;

    @GetMapping("/donations/queue")
    public List<ScheduledDonorResponse> listDonationQueue(
            @RequestParam(value = "from", required = false) OffsetDateTime from) {
        DonationQueueResult queueResult = listDonationQueueUseCase.execute(from);
        return queueResult.items().stream()
                .map(item -> new ScheduledDonorResponse(
                        item.bookingId(), item.visitId(), item.donorId(), item.donorFullName(),
                        item.donorStatus(), item.slotId(), item.purpose(),
                        item.startAt(), item.endAt(), item.location(), item.bookingStatus(),
                        item.medicalDecision(), item.hasDonation(), item.canDonate(),
                        item.donationId(), item.donationPublished(),
                        item.collectionSessionId(), item.collectionSessionStatus(),
                        item.collectionSessionStartedAt(), item.collectionSessionEndedAt(),
                        item.collectionSessionNurseName(), item.collectionSessionPreVitalsJson(),
                        item.collectionSessionPostVitalsJson(), item.collectionSessionNotes(),
                        item.collectionSessionComplications(), item.collectionSessionInterruptionReason()
                ))
                .toList();
    }

    @PostMapping("/collection-sessions")
    public ResponseEntity<CollectionSessionResponse> createSession(
            @AuthenticationPrincipal AccountPrincipal principal,
            @Valid @RequestBody CollectionSessionCreateRequest request) {
        CreateCollectionSessionCommand command = new CreateCollectionSessionCommand(
                principal.getId(), request.visitId(), request.bookingId(),
                request.preVitals(), request.notes()
        );
        CollectionSessionResult result = createCollectionSessionUseCase.execute(command);
        CollectionSessionResponse response = mapToResponse(result);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/collection-sessions/{id}/start")
    public ResponseEntity<CollectionSessionResponse> startSession(
            @AuthenticationPrincipal AccountPrincipal principal,
            @PathVariable UUID id,
            @Valid @RequestBody(required = false) CollectionSessionUpdateRequest request) {
        StartCollectionSessionCommand command = new StartCollectionSessionCommand(
                principal.getId(), id,
                request != null ? request.preVitals() : null,
                request != null ? request.notes() : null
        );
        CollectionSessionResult result = startCollectionSessionUseCase.execute(command);
        return ResponseEntity.ok(mapToResponse(result));
    }

    @PostMapping("/collection-sessions/{id}/complete")
    public ResponseEntity<CollectionSessionResponse> completeSession(
            @AuthenticationPrincipal AccountPrincipal principal,
            @PathVariable UUID id,
            @Valid @RequestBody(required = false) CollectionSessionUpdateRequest request) {
        CompleteCollectionSessionCommand command = new CompleteCollectionSessionCommand(
                principal.getId(), id,
                request != null ? request.postVitals() : null,
                request != null ? request.notes() : null
        );
        CollectionSessionResult result = completeCollectionSessionUseCase.execute(command);
        return ResponseEntity.ok(mapToResponse(result));
    }

    @PostMapping("/collection-sessions/{id}/abort")
    public ResponseEntity<CollectionSessionResponse> abortSession(
            @AuthenticationPrincipal AccountPrincipal principal,
            @PathVariable UUID id,
            @Valid @RequestBody(required = false) CollectionSessionUpdateRequest request) {
        AbortCollectionSessionCommand command = new AbortCollectionSessionCommand(
                principal.getId(), id,
                request != null ? request.interruptionReason() : null,
                request != null ? request.notes() : null
        );
        CollectionSessionResult result = abortCollectionSessionUseCase.execute(command);
        return ResponseEntity.ok(mapToResponse(result));
    }

    @GetMapping("/collection-sessions/{id}")
    public CollectionSessionResponse getSession(@PathVariable UUID id) {
        CollectionSessionResult result = getCollectionSessionUseCase.execute(id);
        return mapToResponse(result);
    }

    private CollectionSessionResponse mapToResponse(CollectionSessionResult result) {
        return new CollectionSessionResponse(
                result.sessionId(), result.visitId(), result.nurseId(), result.nurseName(),
                result.status(), result.startedAt(), result.endedAt(),
                result.preVitals(), result.postVitals(), result.notes(),
                result.complications(), result.interruptionReason(),
                result.createdAt(), result.updatedAt()
        );
    }
}

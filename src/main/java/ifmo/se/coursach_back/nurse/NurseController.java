package ifmo.se.coursach_back.nurse;

import ifmo.se.coursach_back.medical.dto.ScheduledDonorResponse;
import ifmo.se.coursach_back.nurse.dto.CollectionSessionCreateRequest;
import ifmo.se.coursach_back.nurse.dto.CollectionSessionResponse;
import ifmo.se.coursach_back.nurse.dto.CollectionSessionUpdateRequest;
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
    private final NurseWorkflowService nurseWorkflowService;

    @GetMapping("/donations/queue")
    public List<ScheduledDonorResponse> listDonationQueue(
            @RequestParam(value = "from", required = false) OffsetDateTime from) {
        return nurseWorkflowService.listDonationQueue(from);
    }

    @PostMapping("/collection-sessions")
    public ResponseEntity<CollectionSessionResponse> createSession(
            @AuthenticationPrincipal AccountPrincipal principal,
            @Valid @RequestBody CollectionSessionCreateRequest request) {
        CollectionSessionResponse response = nurseWorkflowService.createSession(principal.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/collection-sessions/{id}/start")
    public ResponseEntity<CollectionSessionResponse> startSession(
            @AuthenticationPrincipal AccountPrincipal principal,
            @PathVariable UUID id,
            @RequestBody(required = false) CollectionSessionUpdateRequest request) {
        CollectionSessionResponse response = nurseWorkflowService.startSession(principal.getId(), id, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/collection-sessions/{id}/complete")
    public ResponseEntity<CollectionSessionResponse> completeSession(
            @AuthenticationPrincipal AccountPrincipal principal,
            @PathVariable UUID id,
            @RequestBody(required = false) CollectionSessionUpdateRequest request) {
        CollectionSessionResponse response = nurseWorkflowService.completeSession(principal.getId(), id, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/collection-sessions/{id}/abort")
    public ResponseEntity<CollectionSessionResponse> abortSession(
            @AuthenticationPrincipal AccountPrincipal principal,
            @PathVariable UUID id,
            @RequestBody(required = false) CollectionSessionUpdateRequest request) {
        CollectionSessionResponse response = nurseWorkflowService.abortSession(principal.getId(), id, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/collection-sessions/{id}")
    public CollectionSessionResponse getSession(@PathVariable UUID id) {
        return nurseWorkflowService.getSession(id);
    }
}

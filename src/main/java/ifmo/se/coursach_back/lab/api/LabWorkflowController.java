package ifmo.se.coursach_back.lab.api;

import ifmo.se.coursach_back.lab.api.dto.LabExaminationRequest;
import ifmo.se.coursach_back.lab.api.dto.LabExaminationResponse;
import ifmo.se.coursach_back.lab.api.dto.LabTestResultRequest;
import ifmo.se.coursach_back.lab.api.dto.LabTestResultResponse;
import ifmo.se.coursach_back.lab.api.dto.PendingSampleResponse;
import ifmo.se.coursach_back.lab.application.LabWorkflowService;
import ifmo.se.coursach_back.lab.application.command.PublishLabResultCommand;
import ifmo.se.coursach_back.lab.application.command.RecordLabResultCommand;
import ifmo.se.coursach_back.lab.application.command.SubmitLabExaminationCommand;
import ifmo.se.coursach_back.lab.application.result.LabExaminationRequestResult;
import ifmo.se.coursach_back.lab.application.result.LabExaminationSubmitResult;
import ifmo.se.coursach_back.lab.application.result.LabTestResultResult;
import ifmo.se.coursach_back.lab.application.result.PendingSampleResult;
import ifmo.se.coursach_back.lab.application.usecase.GetSampleResultsUseCase;
import ifmo.se.coursach_back.lab.application.usecase.ListPendingLabRequestsUseCase;
import ifmo.se.coursach_back.lab.application.usecase.ListPendingSamplesUseCase;
import ifmo.se.coursach_back.lab.application.usecase.PublishLabResultUseCase;
import ifmo.se.coursach_back.lab.application.usecase.RecordLabResultUseCase;
import ifmo.se.coursach_back.lab.application.usecase.SubmitLabExaminationUseCase;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/lab")
@RequiredArgsConstructor
@PreAuthorize("hasRole('LAB')")
public class LabWorkflowController {
    private final ListPendingLabRequestsUseCase listPendingLabRequestsUseCase;
    private final ListPendingSamplesUseCase listPendingSamplesUseCase;
    private final RecordLabResultUseCase recordLabResultUseCase;
    private final PublishLabResultUseCase publishLabResultUseCase;
    private final GetSampleResultsUseCase getSampleResultsUseCase;
    private final SubmitLabExaminationUseCase submitLabExaminationUseCase;
    // Keep LabWorkflowService for operations not yet migrated to use cases
    private final LabWorkflowService labWorkflowService;

    @GetMapping("/queue")
    public List<LabExaminationResponse> listAwaitingExamination() {
        LabExaminationRequestResult result = listPendingLabRequestsUseCase.execute();
        return result.items().stream()
                .map(item -> new LabExaminationResponse(
                        item.requestId(), item.visitId(), null, item.donorFullName(),
                        null, null, null, item.status(), null, item.requestedAt(),
                        null, item.completedAt(), item.hemoglobinGl(),
                        item.hematocritPct(), item.rbc10e12L()
                ))
                .toList();
    }

    @GetMapping("/samples")
    public List<PendingSampleResponse> listPendingSamples(@RequestParam(value = "status", required = false) String status) {
        PendingSampleResult result = listPendingSamplesUseCase.execute(status);
        return result.items().stream()
                .map(item -> new PendingSampleResponse(
                        item.sampleId(), item.barcode(), item.status(),
                        item.collectedAt(), null, null, null
                ))
                .toList();
    }

    @PostMapping("/results")
    public ResponseEntity<LabTestResultResponse> recordResult(
            @AuthenticationPrincipal AccountPrincipal principal,
            @Valid @RequestBody LabTestResultRequest request) {
        RecordLabResultCommand command = new RecordLabResultCommand(
                principal.getId(), request.sampleId(), request.testTypeId(),
                request.resultValue(), request.resultFlag()
        );
        LabTestResultResult result = recordLabResultUseCase.execute(command);
        LabTestResultResponse response = new LabTestResultResponse(
                result.resultId(), result.sampleId(), null, result.testTypeCode(),
                result.resultValue(), result.resultFlag(), result.testedAt(),
                result.published(), result.publishedAt()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/results/{resultId}/publish")
    public ResponseEntity<LabTestResultResponse> publishResult(
            @AuthenticationPrincipal AccountPrincipal principal,
            @PathVariable UUID resultId) {
        PublishLabResultCommand command = new PublishLabResultCommand(principal.getId(), resultId);
        LabTestResultResult result = publishLabResultUseCase.execute(command);
        LabTestResultResponse response = new LabTestResultResponse(
                result.resultId(), result.sampleId(), null, result.testTypeCode(),
                result.resultValue(), result.resultFlag(), result.testedAt(),
                result.published(), result.publishedAt()
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/samples/{sampleId}/results")
    public List<LabTestResultResponse> getResultsBySample(@PathVariable UUID sampleId) {
        List<LabTestResultResult> results = getSampleResultsUseCase.execute(sampleId);
        return results.stream()
                .map(r -> new LabTestResultResponse(
                        r.resultId(), r.sampleId(), null, r.testTypeCode(),
                        r.resultValue(), r.resultFlag(), r.testedAt(),
                        r.published(), r.publishedAt()
                ))
                .toList();
    }
    
    @PostMapping("/examinations/{requestId}/results")
    public ResponseEntity<LabExaminationResponse> submitExamination(
            @AuthenticationPrincipal AccountPrincipal principal,
            @PathVariable UUID requestId,
            @Valid @RequestBody LabExaminationRequest request) {
        SubmitLabExaminationCommand command = new SubmitLabExaminationCommand(
                principal.getId(), requestId,
                request.hemoglobinGl(), request.hematocritPct(), request.rbc10e12L()
        );
        LabExaminationSubmitResult result = submitLabExaminationUseCase.execute(command);
        LabExaminationResponse response = new LabExaminationResponse(
                result.requestId(), null, null, null, null, null, null,
                result.status(), null, null, null, result.completedAt(),
                null, null, null
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping("/examinations/pending")
    public List<LabExaminationResponse> listPendingExaminations() {
        LabExaminationRequestResult result = listPendingLabRequestsUseCase.execute();
        return result.items().stream()
                .map(item -> new LabExaminationResponse(
                        item.requestId(), item.visitId(), item.bookingId(), item.donorFullName(),
                        item.slotStartAt(), item.slotEndAt(), item.location(), item.status(), null, item.requestedAt(),
                        null, item.completedAt(), item.hemoglobinGl(),
                        item.hematocritPct(), item.rbc10e12L()
                ))
                .toList();
    }

    @GetMapping("/examinations/requests")
    public List<LabExaminationResponse> listRequests() {
        LabExaminationRequestResult result = listPendingLabRequestsUseCase.execute();
        return result.items().stream()
                .map(item -> new LabExaminationResponse(
                        item.requestId(), item.visitId(), item.bookingId(), item.donorFullName(),
                        item.slotStartAt(), item.slotEndAt(), item.location(), item.status(), null, item.requestedAt(),
                        null, item.completedAt(), item.hemoglobinGl(),
                        item.hematocritPct(), item.rbc10e12L()
                ))
                .toList();
    }
}

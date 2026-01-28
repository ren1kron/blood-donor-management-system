package ifmo.se.coursach_back.lab;

import ifmo.se.coursach_back.lab.dto.LabTestResultRequest;
import ifmo.se.coursach_back.lab.dto.LabTestResultResponse;
import ifmo.se.coursach_back.lab.dto.PendingSampleResponse;
import ifmo.se.coursach_back.model.LabTestResult;
import ifmo.se.coursach_back.model.Sample;
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
    private final LabWorkflowService labWorkflowService;

    @GetMapping("/samples")
    public List<PendingSampleResponse> listPendingSamples(@RequestParam(value = "status", required = false) String status) {
        List<Sample> samples = labWorkflowService.listPendingSamples(status);
        return samples.stream().map(PendingSampleResponse::from).toList();
    }

    @PostMapping("/results")
    public ResponseEntity<LabTestResultResponse> recordResult(
            @AuthenticationPrincipal AccountPrincipal principal,
            @Valid @RequestBody LabTestResultRequest request) {
        LabTestResult result = labWorkflowService.recordResult(principal.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(LabTestResultResponse.from(result));
    }

    @PostMapping("/results/{resultId}/publish")
    public ResponseEntity<LabTestResultResponse> publishResult(
            @AuthenticationPrincipal AccountPrincipal principal,
            @PathVariable UUID resultId) {
        LabTestResult result = labWorkflowService.publishResult(principal.getId(), resultId);
        return ResponseEntity.ok(LabTestResultResponse.from(result));
    }

    @GetMapping("/samples/{sampleId}/results")
    public List<LabTestResultResponse> getResultsBySample(@PathVariable UUID sampleId) {
        List<LabTestResult> results = labWorkflowService.getResultsBySample(sampleId);
        return results.stream().map(LabTestResultResponse::from).toList();
    }
}

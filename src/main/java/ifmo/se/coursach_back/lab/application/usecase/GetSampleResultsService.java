package ifmo.se.coursach_back.lab.application.usecase;

import ifmo.se.coursach_back.lab.application.LabWorkflowService;
import ifmo.se.coursach_back.lab.application.result.LabTestResultResult;
import ifmo.se.coursach_back.lab.domain.LabTestResult;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Implementation of GetSampleResultsUseCase that delegates to LabWorkflowService.
 */
@Service
@RequiredArgsConstructor
public class GetSampleResultsService implements GetSampleResultsUseCase {
    private final LabWorkflowService labWorkflowService;

    @Override
    public List<LabTestResultResult> execute(UUID sampleId) {
        List<LabTestResult> results = labWorkflowService.getResultsBySample(sampleId);
        return results.stream()
                .map(r -> new LabTestResultResult(
                        r.getId(),
                        r.getSample().getId(),
                        r.getTestType().getCode(),
                        r.getTestType().getName(),
                        r.getResultValue(),
                        r.getResultFlag(),
                        r.isPublished(),
                        r.getTestedAt(),
                        r.getPublishedAt()
                ))
                .toList();
    }
}

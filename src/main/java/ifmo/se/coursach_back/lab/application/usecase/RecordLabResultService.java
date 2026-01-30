package ifmo.se.coursach_back.lab.application.usecase;

import ifmo.se.coursach_back.lab.api.dto.LabTestResultRequest;
import ifmo.se.coursach_back.lab.application.LabWorkflowService;
import ifmo.se.coursach_back.lab.application.command.RecordLabResultCommand;
import ifmo.se.coursach_back.lab.application.result.LabTestResultResult;
import ifmo.se.coursach_back.lab.domain.LabTestResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Implementation of RecordLabResultUseCase that delegates to LabWorkflowService.
 */
@Service
@RequiredArgsConstructor
public class RecordLabResultService implements RecordLabResultUseCase {
    private final LabWorkflowService labWorkflowService;

    @Override
    public LabTestResultResult execute(RecordLabResultCommand command) {
        LabTestResultRequest request = new LabTestResultRequest(
                command.sampleId(),
                command.testTypeId(),
                command.resultValue(),
                command.resultFlag()
        );
        LabTestResult result = labWorkflowService.recordResult(command.accountId(), request);
        return mapToResult(result);
    }

    private LabTestResultResult mapToResult(LabTestResult result) {
        return new LabTestResultResult(
                result.getId(),
                result.getSample().getId(),
                result.getTestType().getCode(),
                result.getTestType().getName(),
                result.getResultValue(),
                result.getResultFlag(),
                result.isPublished(),
                result.getTestedAt(),
                result.getPublishedAt()
        );
    }
}

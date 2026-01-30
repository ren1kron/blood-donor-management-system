package ifmo.se.coursach_back.lab.application.usecase;

import ifmo.se.coursach_back.lab.application.LabWorkflowService;
import ifmo.se.coursach_back.lab.application.command.PublishLabResultCommand;
import ifmo.se.coursach_back.lab.application.result.LabTestResultResult;
import ifmo.se.coursach_back.lab.domain.LabTestResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Implementation of PublishLabResultUseCase that delegates to LabWorkflowService.
 */
@Service
@RequiredArgsConstructor
public class PublishLabResultService implements PublishLabResultUseCase {
    private final LabWorkflowService labWorkflowService;

    @Override
    public LabTestResultResult execute(PublishLabResultCommand command) {
        LabTestResult result = labWorkflowService.publishResult(command.accountId(), command.resultId());
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

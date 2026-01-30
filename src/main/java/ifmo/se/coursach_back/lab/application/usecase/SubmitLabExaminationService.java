package ifmo.se.coursach_back.lab.application.usecase;

import ifmo.se.coursach_back.lab.api.dto.LabExaminationRequest;
import ifmo.se.coursach_back.lab.application.LabWorkflowService;
import ifmo.se.coursach_back.lab.application.command.SubmitLabExaminationCommand;
import ifmo.se.coursach_back.lab.application.result.LabExaminationSubmitResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Implementation of SubmitLabExaminationUseCase that delegates to LabWorkflowService.
 */
@Service
@RequiredArgsConstructor
public class SubmitLabExaminationService implements SubmitLabExaminationUseCase {
    private final LabWorkflowService labWorkflowService;

    @Override
    public LabExaminationSubmitResult execute(SubmitLabExaminationCommand command) {
        LabExaminationRequest request = new LabExaminationRequest(
                command.hemoglobinGl(),
                command.hematocritPct(),
                command.rbc10e12L()
        );
        ifmo.se.coursach_back.lab.domain.LabExaminationRequest result = 
                labWorkflowService.submitExamination(command.accountId(), command.requestId(), request);
        return new LabExaminationSubmitResult(
                result.getId(),
                result.getStatus(),
                result.getCompletedAt()
        );
    }
}

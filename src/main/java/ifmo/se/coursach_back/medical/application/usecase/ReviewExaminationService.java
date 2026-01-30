package ifmo.se.coursach_back.medical.application.usecase;

import ifmo.se.coursach_back.medical.api.dto.DeferralRequest;
import ifmo.se.coursach_back.medical.api.dto.ReviewExaminationRequest;
import ifmo.se.coursach_back.medical.application.MedicalWorkflowService;
import ifmo.se.coursach_back.medical.application.command.ReviewExaminationCommand;
import ifmo.se.coursach_back.medical.application.result.ExaminationReviewResult;
import ifmo.se.coursach_back.medical.application.result.MedicalCheckResult;
import ifmo.se.coursach_back.medical.domain.Deferral;
import ifmo.se.coursach_back.medical.domain.MedicalCheck;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReviewExaminationService implements ReviewExaminationUseCase {
    private final MedicalWorkflowService medicalWorkflowService;

    @Override
    public ExaminationReviewResult execute(ReviewExaminationCommand command) {
        DeferralRequest deferralRequest = null;
        if (command.deferral() != null) {
            deferralRequest = new DeferralRequest(
                    command.deferral().deferralType(),
                    command.deferral().reason(),
                    command.deferral().endsAt()
            );
        }
        
        ReviewExaminationRequest request = new ReviewExaminationRequest(
                command.examinationId(),
                command.decision(),
                deferralRequest
        );
        
        MedicalWorkflowService.MedicalCheckResult serviceResult = 
                medicalWorkflowService.reviewExamination(command.accountId(), request);
        
        return toResult(serviceResult.check(), serviceResult.deferral());
    }
    
    private ExaminationReviewResult toResult(MedicalCheck check, Deferral deferral) {
        MedicalCheckResult.DeferralInfo deferralInfo = null;
        if (deferral != null) {
            deferralInfo = new MedicalCheckResult.DeferralInfo(
                    deferral.getId(),
                    deferral.getDeferralType().name(),
                    deferral.getReason(),
                    deferral.getEndsAt()
            );
        }
        return new ExaminationReviewResult(
                check.getId(),
                check.getDecision().name(),
                check.getDecisionAt(),
                deferralInfo
        );
    }
}

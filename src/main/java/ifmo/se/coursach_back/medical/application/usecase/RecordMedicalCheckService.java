package ifmo.se.coursach_back.medical.application.usecase;

import ifmo.se.coursach_back.medical.api.dto.DeferralRequest;
import ifmo.se.coursach_back.medical.api.dto.MedicalCheckRequest;
import ifmo.se.coursach_back.medical.application.MedicalWorkflowService;
import ifmo.se.coursach_back.medical.application.command.RecordMedicalCheckCommand;
import ifmo.se.coursach_back.medical.application.result.MedicalCheckResult;
import ifmo.se.coursach_back.medical.domain.Deferral;
import ifmo.se.coursach_back.medical.domain.MedicalCheck;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RecordMedicalCheckService implements RecordMedicalCheckUseCase {
    private final MedicalWorkflowService medicalWorkflowService;

    @Override
    public MedicalCheckResult execute(RecordMedicalCheckCommand command) {
        DeferralRequest deferralRequest = null;
        if (command.deferral() != null) {
            deferralRequest = new DeferralRequest(
                    command.deferral().deferralType(),
                    command.deferral().reason(),
                    command.deferral().endsAt()
            );
        }
        
        MedicalCheckRequest request = new MedicalCheckRequest(
                command.bookingId(),
                command.visitId(),
                command.weightKg(),
                command.hemoglobinGl(),
                command.systolicMmhg(),
                command.diastolicMmhg(),
                command.decision(),
                deferralRequest
        );
        
        MedicalWorkflowService.MedicalCheckResult serviceResult = 
                medicalWorkflowService.recordMedicalCheck(command.accountId(), request);
        
        return toResult(serviceResult.check(), serviceResult.deferral());
    }
    
    private MedicalCheckResult toResult(MedicalCheck check, Deferral deferral) {
        MedicalCheckResult.DeferralInfo deferralInfo = null;
        if (deferral != null) {
            deferralInfo = new MedicalCheckResult.DeferralInfo(
                    deferral.getId(),
                    deferral.getDeferralType().name(),
                    deferral.getReason(),
                    deferral.getEndsAt()
            );
        }
        return new MedicalCheckResult(
                check.getId(),
                check.getDecision().name(),
                check.getDecisionAt(),
                deferralInfo
        );
    }
}

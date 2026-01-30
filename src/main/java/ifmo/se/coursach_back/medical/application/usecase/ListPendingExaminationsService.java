package ifmo.se.coursach_back.medical.application.usecase;

import ifmo.se.coursach_back.lab.domain.LabExaminationRequest;
import ifmo.se.coursach_back.medical.application.MedicalWorkflowService;
import ifmo.se.coursach_back.medical.application.result.PendingExaminationResult;
import ifmo.se.coursach_back.medical.domain.MedicalCheck;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ListPendingExaminationsService implements ListPendingExaminationsUseCase {
    private final MedicalWorkflowService medicalWorkflowService;

    @Override
    public List<PendingExaminationResult> execute() {
        List<MedicalCheck> pendingChecks = medicalWorkflowService.listPendingExaminations();
        
        if (pendingChecks.isEmpty()) {
            return List.of();
        }
        
        List<UUID> visitIds = pendingChecks.stream()
                .map(check -> check.getVisit().getId())
                .toList();
        
        Map<UUID, LabExaminationRequest> labRequestsByVisitId = 
                medicalWorkflowService.loadLabRequestsByVisitIds(visitIds);
        
        return pendingChecks.stream()
                .map(check -> {
                    LabExaminationRequest labRequest = labRequestsByVisitId.get(check.getVisit().getId());
                    
                    PendingExaminationResult.LabResultInfo labResultInfo = null;
                    if (labRequest != null) {
                        labResultInfo = new PendingExaminationResult.LabResultInfo(
                                labRequest.getId(),
                                labRequest.getStatus().name(),
                                labRequest.getCompletedAt(),
                                null
                        );
                    }
                    
                    return new PendingExaminationResult(
                            check.getId(),
                            check.getVisit().getId(),
                            check.getVisit().getBooking().getDonor().getId(),
                            check.getVisit().getBooking().getDonor().getFullName(),
                            check.getSubmittedAt(),
                            check.getHemoglobinGl(),
                            check.getHematocritPct(),
                            check.getRbc10e12L(),
                            labResultInfo
                    );
                })
                .toList();
    }
}

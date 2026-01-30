package ifmo.se.coursach_back.medical.application.usecase;

import ifmo.se.coursach_back.donor.domain.DonorProfile;
import ifmo.se.coursach_back.medical.application.MedicalWorkflowService;
import ifmo.se.coursach_back.medical.application.command.UpdateDonorStatusCommand;
import ifmo.se.coursach_back.medical.application.result.DonorStatusResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdateDonorStatusService implements UpdateDonorStatusUseCase {
    private final MedicalWorkflowService medicalWorkflowService;

    @Override
    public DonorStatusResult execute(UpdateDonorStatusCommand command) {
        DonorProfile donor = medicalWorkflowService.updateDonorStatus(
                command.donorId(),
                command.donorStatus()
        );
        
        return new DonorStatusResult(
                donor.getId(),
                donor.getDonorStatus().name()
        );
    }
}

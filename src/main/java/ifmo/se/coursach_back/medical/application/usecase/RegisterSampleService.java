package ifmo.se.coursach_back.medical.application.usecase;

import ifmo.se.coursach_back.medical.api.dto.SampleRequest;
import ifmo.se.coursach_back.medical.application.MedicalWorkflowService;
import ifmo.se.coursach_back.medical.application.command.RegisterSampleCommand;
import ifmo.se.coursach_back.medical.application.result.SampleResult;
import ifmo.se.coursach_back.medical.domain.Sample;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RegisterSampleService implements RegisterSampleUseCase {
    private final MedicalWorkflowService medicalWorkflowService;

    @Override
    public SampleResult execute(RegisterSampleCommand command) {
        SampleRequest request = new SampleRequest(
                command.donationId(),
                command.sampleCode(),
                command.status(),
                command.quarantineReason(),
                command.rejectionReason()
        );
        
        Sample sample = medicalWorkflowService.registerSample(request);
        
        return new SampleResult(
                sample.getId(),
                sample.getDonation().getId(),
                sample.getSampleCode(),
                sample.getStatus() != null ? sample.getStatus().name() : null,
                sample.getCollectedAt()
        );
    }
}

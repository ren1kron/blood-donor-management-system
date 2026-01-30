package ifmo.se.coursach_back.medical.application.usecase;

import ifmo.se.coursach_back.medical.api.dto.DonationRequest;
import ifmo.se.coursach_back.medical.application.MedicalWorkflowService;
import ifmo.se.coursach_back.medical.application.command.RecordDonationCommand;
import ifmo.se.coursach_back.medical.application.result.DonationResult;
import ifmo.se.coursach_back.medical.domain.Donation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RecordDonationService implements RecordDonationUseCase {
    private final MedicalWorkflowService medicalWorkflowService;

    @Override
    public DonationResult execute(RecordDonationCommand command) {
        DonationRequest request = new DonationRequest(
                command.bookingId(),
                command.visitId(),
                command.donationType(),
                command.volumeMl(),
                command.performedAt()
        );
        
        Donation donation = medicalWorkflowService.recordDonation(command.accountId(), request);
        
        return new DonationResult(
                donation.getId(),
                donation.getVisit().getId(),
                donation.getDonationType(),
                donation.getVolumeMl(),
                donation.isPublished(),
                donation.getPerformedAt(),
                donation.getPublishedAt()
        );
    }
}

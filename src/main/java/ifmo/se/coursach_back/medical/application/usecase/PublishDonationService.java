package ifmo.se.coursach_back.medical.application.usecase;

import ifmo.se.coursach_back.medical.application.MedicalWorkflowService;
import ifmo.se.coursach_back.medical.application.command.PublishDonationCommand;
import ifmo.se.coursach_back.medical.application.result.DonationResult;
import ifmo.se.coursach_back.medical.domain.Donation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PublishDonationService implements PublishDonationUseCase {
    private final MedicalWorkflowService medicalWorkflowService;

    @Override
    public DonationResult execute(PublishDonationCommand command) {
        Donation donation = medicalWorkflowService.publishDonation(
                command.accountId(),
                command.donationId()
        );
        
        return new DonationResult(
                donation.getId(),
                donation.getVisit().getId(),
                donation.getDonationType() != null ? donation.getDonationType().getValue() : null,
                donation.getVolumeMl(),
                donation.isPublished(),
                donation.getPerformedAt(),
                donation.getPublishedAt()
        );
    }
}

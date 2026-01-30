package ifmo.se.coursach_back.medical.application.usecase;

import ifmo.se.coursach_back.medical.application.command.PublishDonationCommand;
import ifmo.se.coursach_back.medical.application.result.DonationResult;

/**
 * Use case for publishing a donation.
 */
public interface PublishDonationUseCase {
    DonationResult execute(PublishDonationCommand command);
}

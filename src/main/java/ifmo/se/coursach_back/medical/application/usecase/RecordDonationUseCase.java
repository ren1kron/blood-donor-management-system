package ifmo.se.coursach_back.medical.application.usecase;

import ifmo.se.coursach_back.medical.application.command.RecordDonationCommand;
import ifmo.se.coursach_back.medical.application.result.DonationResult;

/**
 * Use case for recording a donation.
 */
public interface RecordDonationUseCase {
    DonationResult execute(RecordDonationCommand command);
}

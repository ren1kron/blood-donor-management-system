package ifmo.se.coursach_back.donor.application.usecase;

import ifmo.se.coursach_back.donor.application.command.UpdateDonorProfileCommand;
import ifmo.se.coursach_back.donor.application.result.DonorProfileResult;

/**
 * Use case for updating donor profile.
 */
public interface UpdateDonorProfileUseCase {
    DonorProfileResult execute(UpdateDonorProfileCommand command);
}

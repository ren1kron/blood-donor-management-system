package ifmo.se.coursach_back.medical.application.usecase;

import ifmo.se.coursach_back.medical.application.command.UpdateDonorStatusCommand;
import ifmo.se.coursach_back.medical.application.result.DonorStatusResult;

/**
 * Use case for updating donor status.
 */
public interface UpdateDonorStatusUseCase {
    DonorStatusResult execute(UpdateDonorStatusCommand command);
}

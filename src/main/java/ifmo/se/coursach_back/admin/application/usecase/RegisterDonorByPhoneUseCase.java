package ifmo.se.coursach_back.admin.application.usecase;

import ifmo.se.coursach_back.admin.application.command.RegisterDonorByPhoneCommand;
import ifmo.se.coursach_back.admin.application.result.RegisterDonorResult;

/**
 * Use case interface for registering a donor by phone.
 */
public interface RegisterDonorByPhoneUseCase {
    /**
     * Register a new donor by phone.
     * @param command the command containing donor registration details
     * @return the donor registration result
     */
    RegisterDonorResult execute(RegisterDonorByPhoneCommand command);
}

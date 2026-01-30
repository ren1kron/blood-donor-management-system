package ifmo.se.coursach_back.admin.application.usecase;

import ifmo.se.coursach_back.admin.application.command.CreateStaffProfileCommand;
import ifmo.se.coursach_back.admin.application.result.CreateStaffProfileResult;

/**
 * Use case interface for creating a staff profile.
 */
public interface CreateStaffProfileUseCase {
    /**
     * Create a new staff profile.
     * @param command the command containing staff profile details
     * @return the created staff profile result
     */
    CreateStaffProfileResult execute(CreateStaffProfileCommand command);
}

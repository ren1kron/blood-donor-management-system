package ifmo.se.coursach_back.admin.application.usecase;

import ifmo.se.coursach_back.admin.application.command.UpdateAccountCommand;

/**
 * Use case interface for updating an account.
 */
public interface UpdateAccountUseCase {
    /**
     * Update an account.
     * @param command the command containing account update details
     */
    void execute(UpdateAccountCommand command);
}

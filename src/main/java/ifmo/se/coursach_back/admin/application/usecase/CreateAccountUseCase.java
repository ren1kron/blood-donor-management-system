package ifmo.se.coursach_back.admin.application.usecase;

import ifmo.se.coursach_back.admin.application.command.CreateAccountCommand;
import ifmo.se.coursach_back.admin.application.result.CreateAccountResult;

/**
 * Use case interface for creating a new account.
 */
public interface CreateAccountUseCase {
    /**
     * Create a new account.
     * @param command the command containing account details
     * @return the created account result
     */
    CreateAccountResult execute(CreateAccountCommand command);
}

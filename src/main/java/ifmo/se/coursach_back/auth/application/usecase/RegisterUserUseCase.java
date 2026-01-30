package ifmo.se.coursach_back.auth.application.usecase;

import ifmo.se.coursach_back.auth.application.command.RegisterUserCommand;
import ifmo.se.coursach_back.auth.application.result.AuthResult;

/**
 * Use case interface for user registration.
 */
public interface RegisterUserUseCase {
    /**
     * Register a new user (donor) in the system.
     * @param command registration command with user details
     * @return authentication result with token and account info
     */
    AuthResult execute(RegisterUserCommand command);
}

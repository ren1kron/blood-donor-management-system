package ifmo.se.coursach_back.auth.application.usecase;

import ifmo.se.coursach_back.auth.application.command.LoginUserCommand;
import ifmo.se.coursach_back.auth.application.result.AuthResult;

/**
 * Use case interface for user login/authentication.
 */
public interface LoginUserUseCase {
    /**
     * Authenticate a user and generate JWT token.
     * @param command login command with credentials
     * @return authentication result with token and account info
     */
    AuthResult execute(LoginUserCommand command);
}

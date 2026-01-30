package ifmo.se.coursach_back.auth.application.usecase;

import ifmo.se.coursach_back.auth.api.dto.LoginRequest;
import ifmo.se.coursach_back.auth.application.AuthService;
import ifmo.se.coursach_back.auth.application.command.LoginUserCommand;
import ifmo.se.coursach_back.auth.application.result.AuthResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Implementation of LoginUserUseCase that delegates to AuthService.
 */
@Service
@RequiredArgsConstructor
public class LoginUserService implements LoginUserUseCase {
    private final AuthService authService;

    @Override
    public AuthResult execute(LoginUserCommand command) {
        LoginRequest request = new LoginRequest(command.identifier(), command.password());
        var response = authService.login(request);
        return new AuthResult(response.token(), response.accountId(), response.roles());
    }
}

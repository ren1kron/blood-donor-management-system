package ifmo.se.coursach_back.auth.application.usecase;

import ifmo.se.coursach_back.auth.api.dto.RegisterRequest;
import ifmo.se.coursach_back.auth.application.AuthService;
import ifmo.se.coursach_back.auth.application.command.RegisterUserCommand;
import ifmo.se.coursach_back.auth.application.result.AuthResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Implementation of RegisterUserUseCase that delegates to AuthService.
 */
@Service
@RequiredArgsConstructor
public class RegisterUserService implements RegisterUserUseCase {
    private final AuthService authService;

    @Override
    public AuthResult execute(RegisterUserCommand command) {
        RegisterRequest request = new RegisterRequest(
                command.email(),
                command.phone(),
                command.password(),
                command.fullName(),
                command.birthDate(),
                command.bloodGroup(),
                command.rhFactor()
        );
        var response = authService.register(request);
        return new AuthResult(response.token(), response.accountId(), response.roles());
    }
}

package ifmo.se.coursach_back.auth.api;

import ifmo.se.coursach_back.auth.api.dto.AccountProfileResponse;
import ifmo.se.coursach_back.auth.api.dto.AuthResponse;
import ifmo.se.coursach_back.auth.api.dto.LoginRequest;
import ifmo.se.coursach_back.auth.api.dto.RegisterRequest;
import ifmo.se.coursach_back.auth.application.command.LoginUserCommand;
import ifmo.se.coursach_back.auth.application.command.RegisterUserCommand;
import ifmo.se.coursach_back.auth.application.result.AuthResult;
import ifmo.se.coursach_back.auth.application.result.ProfileResult;
import ifmo.se.coursach_back.auth.application.usecase.GetCurrentProfileUseCase;
import ifmo.se.coursach_back.auth.application.usecase.LoginUserUseCase;
import ifmo.se.coursach_back.auth.application.usecase.RegisterUserUseCase;
import ifmo.se.coursach_back.security.AccountPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final RegisterUserUseCase registerUserUseCase;
    private final LoginUserUseCase loginUserUseCase;
    private final GetCurrentProfileUseCase getCurrentProfileUseCase;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        RegisterUserCommand command = new RegisterUserCommand(
                request.email(), request.phone(), request.password(),
                request.fullName(), request.birthDate(), request.bloodGroup(), request.rhFactor()
        );
        AuthResult result = registerUserUseCase.execute(command);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new AuthResponse(result.token(), result.accountId(), result.roles()));
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        LoginUserCommand command = new LoginUserCommand(request.identifier(), request.password());
        AuthResult result = loginUserUseCase.execute(command);
        return new AuthResponse(result.token(), result.accountId(), result.roles());
    }

    @GetMapping("/me")
    public AccountProfileResponse me(@AuthenticationPrincipal AccountPrincipal principal) {
        ProfileResult result = getCurrentProfileUseCase.execute(principal.getId());
        return new AccountProfileResponse(
                result.accountId(), result.email(), result.phone(),
                result.roles(), result.profileType(), result.fullName()
        );
    }

}

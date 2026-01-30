package ifmo.se.coursach_back.auth.application.usecase;

import ifmo.se.coursach_back.auth.application.AuthService;
import ifmo.se.coursach_back.auth.application.result.ProfileResult;
import ifmo.se.coursach_back.security.AccountPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Implementation of GetCurrentProfileUseCase that delegates to AuthService.
 */
@Service
@RequiredArgsConstructor
public class GetCurrentProfileService implements GetCurrentProfileUseCase {
    private final AuthService authService;

    @Override
    public ProfileResult execute(UUID accountId) {
        // Create a minimal AccountPrincipal for the service call
        AccountPrincipal principal = new AccountPrincipal(accountId, null, null, null, true, java.util.List.of());
        var response = authService.currentProfile(principal);
        return new ProfileResult(
                response.accountId(),
                response.email(),
                response.phone(),
                response.roles(),
                response.profileType(),
                response.fullName()
        );
    }
}

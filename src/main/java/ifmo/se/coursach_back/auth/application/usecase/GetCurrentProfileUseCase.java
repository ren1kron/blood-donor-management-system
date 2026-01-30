package ifmo.se.coursach_back.auth.application.usecase;

import ifmo.se.coursach_back.auth.application.result.ProfileResult;
import java.util.UUID;

/**
 * Use case interface for retrieving current user profile.
 */
public interface GetCurrentProfileUseCase {
    /**
     * Get the profile of the currently authenticated user.
     * @param accountId the account ID of the current user
     * @return profile information
     */
    ProfileResult execute(UUID accountId);
}

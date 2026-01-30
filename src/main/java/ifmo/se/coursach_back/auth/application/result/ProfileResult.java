package ifmo.se.coursach_back.auth.application.result;

import java.util.List;
import java.util.UUID;

/**
 * Result object for profile retrieval use case.
 */
public record ProfileResult(
        UUID accountId,
        String email,
        String phone,
        List<String> roles,
        String profileType,
        String fullName
) {
}

package ifmo.se.coursach_back.auth.application.result;

import java.util.List;
import java.util.UUID;

/**
 * Result object for authentication use cases.
 */
public record AuthResult(
        String token,
        UUID accountId,
        List<String> roles
) {
}

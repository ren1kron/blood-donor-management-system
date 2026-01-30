package ifmo.se.coursach_back.admin.application.result;

import java.util.UUID;

/**
 * Result object for account creation.
 */
public record CreateAccountResult(
        UUID accountId
) {
}

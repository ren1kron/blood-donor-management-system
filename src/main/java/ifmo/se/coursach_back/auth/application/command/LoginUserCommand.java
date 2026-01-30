package ifmo.se.coursach_back.auth.application.command;

/**
 * Command object for user login use case.
 */
public record LoginUserCommand(
        String identifier,
        String password
) {
}

package ifmo.se.coursach_back.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when there is a conflict with the current state of a resource.
 * Maps to HTTP 409 Conflict.
 */
public class ConflictException extends ApplicationException {

    private static final String CODE = "CONFLICT";

    public ConflictException(String message) {
        super(CODE, HttpStatus.CONFLICT, message);
    }

    public ConflictException(String message, Object details) {
        super(CODE, HttpStatus.CONFLICT, message, details);
    }

    public ConflictException(String message, Throwable cause) {
        super(CODE, HttpStatus.CONFLICT, message, null, cause);
    }

    public static ConflictException duplicate(String entity, String field, String value) {
        return new ConflictException(
                String.format("%s with %s '%s' already exists", entity, field, value)
        );
    }

    public static ConflictException alreadyInState(String entity, String state) {
        return new ConflictException(
                String.format("%s is already in state: %s", entity, state)
        );
    }
}

package ifmo.se.coursach_back.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when request validation fails.
 * Maps to HTTP 422 Unprocessable Entity.
 */
public class ValidationException extends ApplicationException {

    private static final String CODE = "VALIDATION_ERROR";

    public ValidationException(String message) {
        super(CODE, HttpStatus.UNPROCESSABLE_ENTITY, message);
    }

    public ValidationException(String message, Object details) {
        super(CODE, HttpStatus.UNPROCESSABLE_ENTITY, message, details);
    }

    public static ValidationException invalidField(String field, String reason) {
        return new ValidationException(String.format("Invalid %s: %s", field, reason));
    }

    public static ValidationException missingField(String field) {
        return new ValidationException(String.format("Missing required field: %s", field));
    }

    public static ValidationException invalidState(String message) {
        return new ValidationException(message);
    }
}

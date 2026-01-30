package ifmo.se.coursach_back.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when a request is malformed or contains invalid data.
 * Maps to HTTP 400 Bad Request.
 */
public class BadRequestException extends ApplicationException {

    private static final String CODE = "BAD_REQUEST";

    public BadRequestException(String message) {
        super(CODE, HttpStatus.BAD_REQUEST, message);
    }

    public BadRequestException(String message, Object details) {
        super(CODE, HttpStatus.BAD_REQUEST, message, details);
    }

    public BadRequestException(String message, Throwable cause) {
        super(CODE, HttpStatus.BAD_REQUEST, message, null, cause);
    }

    public static BadRequestException invalidParameter(String parameter, String reason) {
        return new BadRequestException(
                String.format("Invalid parameter '%s': %s", parameter, reason)
        );
    }

    public static BadRequestException malformedRequest(String details) {
        return new BadRequestException("Malformed request: " + details);
    }
}

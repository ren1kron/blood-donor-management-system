package ifmo.se.coursach_back.exception;

import org.springframework.http.HttpStatus;

import java.util.UUID;

/**
 * Exception thrown when a requested resource is not found.
 * Maps to HTTP 404 Not Found.
 */
public class NotFoundException extends ApplicationException {

    private static final String CODE = "NOT_FOUND";

    public NotFoundException(String message) {
        super(CODE, HttpStatus.NOT_FOUND, message);
    }

    public NotFoundException(String message, Object details) {
        super(CODE, HttpStatus.NOT_FOUND, message, details);
    }

    public NotFoundException(String message, Throwable cause) {
        super(CODE, HttpStatus.NOT_FOUND, message, null, cause);
    }

    public static NotFoundException entity(String entityName, UUID id) {
        return new NotFoundException(
                String.format("%s not found with id: %s", entityName, id)
        );
    }

    public static NotFoundException entity(String entityName, String identifier) {
        return new NotFoundException(
                String.format("%s not found: %s", entityName, identifier)
        );
    }

    public static NotFoundException resource(String resource) {
        return new NotFoundException(String.format("Resource not found: %s", resource));
    }
}

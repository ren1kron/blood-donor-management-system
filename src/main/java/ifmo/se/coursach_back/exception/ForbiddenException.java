package ifmo.se.coursach_back.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when access to a resource is forbidden.
 * Maps to HTTP 403 Forbidden.
 */
public class ForbiddenException extends ApplicationException {

    private static final String CODE = "FORBIDDEN";

    public ForbiddenException(String message) {
        super(CODE, HttpStatus.FORBIDDEN, message);
    }

    public ForbiddenException(String message, Object details) {
        super(CODE, HttpStatus.FORBIDDEN, message, details);
    }

    public static ForbiddenException accessDenied() {
        return new ForbiddenException("Access denied");
    }

    public static ForbiddenException insufficientPermissions(String resource) {
        return new ForbiddenException("Insufficient permissions to access: " + resource);
    }

    public static ForbiddenException notOwner(String resource) {
        return new ForbiddenException("You are not the owner of: " + resource);
    }
}

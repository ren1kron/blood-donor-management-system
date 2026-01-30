package ifmo.se.coursach_back.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when business logic rules are violated.
 * Maps to HTTP 409 Conflict.
 */
public class BusinessRuleException extends ApplicationException {

    private static final String CODE = "BUSINESS_RULE_VIOLATION";

    public BusinessRuleException(String message) {
        super(CODE, HttpStatus.CONFLICT, message);
    }

    public BusinessRuleException(String message, Object details) {
        super(CODE, HttpStatus.CONFLICT, message, details);
    }

    public static BusinessRuleException alreadyExists(String entity, String identifier) {
        return new BusinessRuleException(
                String.format("%s already exists: %s", entity, identifier)
        );
    }

    public static BusinessRuleException invalidTransition(String from, String to) {
        return new BusinessRuleException(
                String.format("Invalid state transition from %s to %s", from, to)
        );
    }

    public static BusinessRuleException operationNotAllowed(String operation, String reason) {
        return new BusinessRuleException(
                String.format("Operation '%s' not allowed: %s", operation, reason)
        );
    }
}

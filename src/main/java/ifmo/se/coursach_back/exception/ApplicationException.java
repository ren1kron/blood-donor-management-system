package ifmo.se.coursach_back.exception;

import org.springframework.http.HttpStatus;

/**
 * Base exception for all application-specific exceptions.
 * Provides consistent error handling across the application.
 */
public abstract class ApplicationException extends RuntimeException {

    private final String code;
    private final HttpStatus status;
    private final Object details;

    protected ApplicationException(String code, HttpStatus status, String message) {
        this(code, status, message, null, null);
    }

    protected ApplicationException(String code, HttpStatus status, String message, Object details) {
        this(code, status, message, details, null);
    }

    protected ApplicationException(String code, HttpStatus status, String message, Object details, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.status = status;
        this.details = details;
    }

    public String getCode() {
        return code;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public Object getDetails() {
        return details;
    }
}

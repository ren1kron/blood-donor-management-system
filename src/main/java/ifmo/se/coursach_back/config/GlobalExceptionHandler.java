package ifmo.se.coursach_back.config;

import ifmo.se.coursach_back.exception.ApplicationException;
import ifmo.se.coursach_back.exception.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Global exception handler for consistent error responses across the application.
 * All errors follow unified ErrorResponse format with code, message, details, timestamp, path.
 * 
 * Validation approach: 400 BAD_REQUEST for all input validation errors.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // ==================== Application Exceptions ====================

    /**
     * Handles all application-specific exceptions (NotFoundException, BadRequestException, etc.)
     */
    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<ErrorResponse> handleApplicationException(
            ApplicationException ex, HttpServletRequest request) {
        log.debug("Application exception: {} - {}", ex.getCode(), ex.getMessage());
        
        ErrorResponse response = ErrorResponse.of(
                ex.getCode(),
                ex.getMessage(),
                ex.getDetails(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(response, ex.getStatus());
    }

    // ==================== Validation Exceptions (400) ====================

    /**
     * Handles @Valid annotation validation errors on request body.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        
        Map<String, String> fieldErrors = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                fieldErrors.put(error.getField(), error.getDefaultMessage())
        );

        log.debug("Validation failed on {}: {}", request.getRequestURI(), fieldErrors);
        ErrorResponse response = ErrorResponse.validation(fieldErrors, request.getRequestURI());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles @Validated constraint violations on path/query parameters.
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(
            ConstraintViolationException ex, HttpServletRequest request) {
        
        Map<String, String> violations = new LinkedHashMap<>();
        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            String path = violation.getPropertyPath().toString();
            String field = path.contains(".") ? path.substring(path.lastIndexOf('.') + 1) : path;
            violations.put(field, violation.getMessage());
        }

        log.debug("Constraint violation on {}: {}", request.getRequestURI(), violations);
        ErrorResponse response = ErrorResponse.validation(violations, request.getRequestURI());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles missing required request parameters.
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParameter(
            MissingServletRequestParameterException ex, HttpServletRequest request) {
        
        Map<String, String> details = Map.of(ex.getParameterName(), "Required parameter is missing");
        log.debug("Missing parameter on {}: {}", request.getRequestURI(), ex.getParameterName());
        
        ErrorResponse response = ErrorResponse.of(
                "VALIDATION_ERROR",
                "Missing required parameter: " + ex.getParameterName(),
                details,
                request.getRequestURI()
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles type mismatch for path/query parameters.
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        
        String expectedType = ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown";
        Map<String, String> details = Map.of(
                ex.getName(), "Expected type: " + expectedType + ", got: " + ex.getValue()
        );
        
        log.debug("Type mismatch on {}: {}", request.getRequestURI(), details);
        ErrorResponse response = ErrorResponse.of(
                "VALIDATION_ERROR",
                "Invalid parameter type for: " + ex.getName(),
                details,
                request.getRequestURI()
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles malformed JSON or unreadable request body.
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleMessageNotReadable(
            HttpMessageNotReadableException ex, HttpServletRequest request) {
        
        log.debug("Malformed request body on {}: {}", request.getRequestURI(), ex.getMessage());
        ErrorResponse response = ErrorResponse.of(
                "VALIDATION_ERROR",
                "Malformed request body",
                null,
                request.getRequestURI()
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles IllegalArgumentException (e.g., invalid enum values).
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(
            IllegalArgumentException ex, HttpServletRequest request) {
        
        log.debug("Illegal argument on {}: {}", request.getRequestURI(), ex.getMessage());
        ErrorResponse response = ErrorResponse.of(
                "BAD_REQUEST",
                ex.getMessage(),
                null,
                request.getRequestURI()
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // ==================== Security Exceptions ====================

    /**
     * Handles authentication failures.
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthentication(
            AuthenticationException ex, HttpServletRequest request) {
        
        log.debug("Authentication failed on {}: {}", request.getRequestURI(), ex.getMessage());
        ErrorResponse response = ErrorResponse.of(
                "UNAUTHORIZED",
                "Authentication required",
                null,
                request.getRequestURI()
        );
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    /**
     * Handles authorization failures.
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(
            AccessDeniedException ex, HttpServletRequest request) {
        
        log.debug("Access denied on {}: {}", request.getRequestURI(), ex.getMessage());
        ErrorResponse response = ErrorResponse.of(
                "FORBIDDEN",
                "Access denied",
                null,
                request.getRequestURI()
        );
        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }

    // ==================== HTTP Exceptions ====================

    /**
     * Handles unsupported HTTP methods.
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotSupported(
            HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {
        
        log.debug("Method not allowed on {}: {}", request.getRequestURI(), ex.getMethod());
        ErrorResponse response = ErrorResponse.of(
                "METHOD_NOT_ALLOWED",
                "HTTP method " + ex.getMethod() + " is not supported for this endpoint",
                null,
                request.getRequestURI()
        );
        return new ResponseEntity<>(response, HttpStatus.METHOD_NOT_ALLOWED);
    }

    /**
     * Handles unsupported media types.
     */
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMediaTypeNotSupported(
            HttpMediaTypeNotSupportedException ex, HttpServletRequest request) {
        
        log.debug("Unsupported media type on {}: {}", request.getRequestURI(), ex.getContentType());
        ErrorResponse response = ErrorResponse.of(
                "UNSUPPORTED_MEDIA_TYPE",
                "Content type " + ex.getContentType() + " is not supported",
                null,
                request.getRequestURI()
        );
        return new ResponseEntity<>(response, HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }

    /**
     * Handles resource not found (static resources, wrong paths).
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoResourceFound(
            NoResourceFoundException ex, HttpServletRequest request) {
        
        log.debug("Resource not found: {}", request.getRequestURI());
        ErrorResponse response = ErrorResponse.of(
                "NOT_FOUND",
                "Resource not found",
                null,
                request.getRequestURI()
        );
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    // ==================== Legacy & State Exceptions ====================

    /**
     * Handles ResponseStatusException (legacy support).
     */
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> handleResponseStatus(
            ResponseStatusException ex, HttpServletRequest request) {
        
        HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());
        String message = ex.getReason() != null ? ex.getReason() : status.getReasonPhrase();
        
        log.debug("ResponseStatusException on {}: {} - {}", request.getRequestURI(), status, message);
        ErrorResponse response = ErrorResponse.of(
                status.name(),
                message,
                null,
                request.getRequestURI()
        );
        return new ResponseEntity<>(response, status);
    }

    /**
     * Handles illegal state (business logic conflicts).
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalState(
            IllegalStateException ex, HttpServletRequest request) {
        
        log.debug("Illegal state on {}: {}", request.getRequestURI(), ex.getMessage());
        ErrorResponse response = ErrorResponse.of(
                "CONFLICT",
                ex.getMessage(),
                null,
                request.getRequestURI()
        );
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    // ==================== Catch-all Handler ====================

    /**
     * Catch-all handler for unexpected exceptions.
     * Logs full stack trace but returns generic message to client.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex, HttpServletRequest request) {
        
        log.error("Unexpected error on {}: {}", request.getRequestURI(), ex.getMessage(), ex);
        ErrorResponse response = ErrorResponse.internal(request.getRequestURI());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

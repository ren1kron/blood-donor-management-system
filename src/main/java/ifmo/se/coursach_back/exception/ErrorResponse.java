package ifmo.se.coursach_back.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.OffsetDateTime;
import java.util.Map;

/**
 * Unified error response format for all API errors.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
        String code,
        String message,
        Object details,
        OffsetDateTime timestamp,
        String path
) {
    /**
     * Creates ErrorResponse with current timestamp.
     */
    public static ErrorResponse of(String code, String message, Object details, String path) {
        return new ErrorResponse(code, message, details, OffsetDateTime.now(), path);
    }

    /**
     * Creates ErrorResponse for validation errors.
     */
    public static ErrorResponse validation(Map<String, String> fieldErrors, String path) {
        return new ErrorResponse("VALIDATION_ERROR", "Validation failed", fieldErrors, OffsetDateTime.now(), path);
    }

    /**
     * Creates ErrorResponse for internal errors (no details exposed).
     */
    public static ErrorResponse internal(String path) {
        return new ErrorResponse(
                "INTERNAL_ERROR",
                "An unexpected error occurred. Please try again later.",
                null,
                OffsetDateTime.now(),
                path
        );
    }
}

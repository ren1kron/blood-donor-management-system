package ifmo.se.coursach_back.exception;

import java.time.OffsetDateTime;
import java.util.UUID;

public record ErrorResponse(
        String code,
        String message,
        Object details,
        String requestId,
        OffsetDateTime timestamp
) {
    public ErrorResponse(String code, String message, Object details) {
        this(code, message, details, UUID.randomUUID().toString(), OffsetDateTime.now());
    }
}

package ifmo.se.coursach_back.exception;

public record ErrorResponse(
        String code,
        String message,
        Object details
) {
}

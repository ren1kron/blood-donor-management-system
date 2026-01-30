package ifmo.se.coursach_back.admin.application.result;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Result object for expired document.
 */
public record ExpiredDocumentResult(
        UUID documentId,
        UUID donorId,
        String fullName,
        String phone,
        String email,
        String docType,
        LocalDate expiresAt
) {
}

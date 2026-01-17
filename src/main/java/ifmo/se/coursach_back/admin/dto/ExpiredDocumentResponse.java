package ifmo.se.coursach_back.admin.dto;

import java.time.LocalDate;
import java.util.UUID;

public record ExpiredDocumentResponse(
        UUID documentId,
        UUID donorId,
        String fullName,
        String phone,
        String email,
        String docType,
        LocalDate expiresAt
) {
}

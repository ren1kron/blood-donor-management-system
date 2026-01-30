package ifmo.se.coursach_back.admin.api.dto;

import ifmo.se.coursach_back.donor.domain.DocumentType;
import java.time.LocalDate;
import java.util.UUID;

public record ExpiredDocumentRow(
        UUID documentId,
        UUID donorId,
        String fullName,
        String phone,
        String email,
        DocumentType docType,
        LocalDate expiresAt
) {
}

package ifmo.se.coursach_back.lab.api.dto;

import jakarta.validation.constraints.Size;
import java.time.OffsetDateTime;

/**
 * Request DTO for lab to review/update a blood unit.
 */
public record ReviewBloodUnitRequest(
        String bloodGroup,
        String rhFactor,
        Short componentTypeId,
        OffsetDateTime expiresAt,
        String status,
        @Size(max = 500, message = "Storage location must not exceed 500 characters")
        String storageLocation,
        @Size(max = 1000, message = "Quarantine reason must not exceed 1000 characters")
        String quarantineReason
) {
}

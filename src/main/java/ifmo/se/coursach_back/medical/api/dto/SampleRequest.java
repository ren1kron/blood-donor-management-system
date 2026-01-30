package ifmo.se.coursach_back.medical.api.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;

public record SampleRequest(
        @NotNull(message = "Donation ID is required")
        UUID donationId,
        @Size(max = 50, message = "Sample code must not exceed 50 characters")
        String sampleCode,  // Optional - auto-generated if not provided
        String status,
        @Size(max = 500, message = "Quarantine reason must not exceed 500 characters")
        String quarantineReason,
        @Size(max = 500, message = "Rejection reason must not exceed 500 characters")
        String rejectionReason
) {
}

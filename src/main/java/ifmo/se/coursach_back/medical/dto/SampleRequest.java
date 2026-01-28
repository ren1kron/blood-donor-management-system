package ifmo.se.coursach_back.medical.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record SampleRequest(
        @NotNull UUID donationId,
        String sampleCode,  // Optional - auto-generated if not provided
        String status,
        String quarantineReason,
        String rejectionReason
) {
}

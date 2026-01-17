package ifmo.se.coursach_back.medical.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record SampleRequest(
        @NotNull UUID donationId,
        @NotBlank String sampleCode,
        String status,
        String quarantineReason,
        String rejectionReason
) {
}

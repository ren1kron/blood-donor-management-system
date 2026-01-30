package ifmo.se.coursach_back.medical.api.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record ReviewExaminationRequest(
        @NotNull(message = "Examination ID is required")
        UUID examinationId,
        @NotBlank(message = "Decision is required")
        String decision,
        @Valid DeferralRequest deferral
) {
}

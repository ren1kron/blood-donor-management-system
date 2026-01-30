package ifmo.se.coursach_back.medical.api.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

public record ExaminationDecisionRequest(
        @NotBlank(message = "Decision is required")
        String decision,
        @Valid DeferralRequest deferral
) {
}

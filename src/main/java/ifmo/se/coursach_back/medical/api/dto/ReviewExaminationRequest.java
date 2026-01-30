package ifmo.se.coursach_back.medical.api.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record ReviewExaminationRequest(
        @NotNull UUID examinationId,
        @NotBlank String decision,
        @Valid DeferralRequest deferral
) {
}

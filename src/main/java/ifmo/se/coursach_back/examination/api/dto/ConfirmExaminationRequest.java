package ifmo.se.coursach_back.examination.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ConfirmExaminationRequest(
        @NotBlank(message = "consentType is required")
        String consentType,
        
        @NotNull(message = "questionnairePayload is required")
        QuestionnairePayload questionnairePayload,
        
        @NotNull(message = "consentGiven must be true")
        Boolean consentGiven
) {}

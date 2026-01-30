package ifmo.se.coursach_back.examination.api.dto;

import jakarta.validation.constraints.NotNull;

public record QuestionnairePayload(
        @NotNull(message = "hasFever is required")
        Boolean hasFever,
        
        @NotNull(message = "tookAntibioticsLast14Days is required")
        Boolean tookAntibioticsLast14Days,
        
        @NotNull(message = "hasChronicDiseases is required")
        Boolean hasChronicDiseases,
        
        String comment
) {}

package ifmo.se.coursach_back.lab.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record LabTestResultRequest(
        @NotNull UUID sampleId,
        @NotNull Short testTypeId,
        String resultValue,
        @NotBlank String resultFlag
) {
}

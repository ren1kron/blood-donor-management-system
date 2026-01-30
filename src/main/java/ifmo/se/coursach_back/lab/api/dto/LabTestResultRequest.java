package ifmo.se.coursach_back.lab.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;

public record LabTestResultRequest(
        @NotNull(message = "Sample ID is required")
        UUID sampleId,
        @NotNull(message = "Test type ID is required")
        Short testTypeId,
        @Size(max = 500, message = "Result value must not exceed 500 characters")
        String resultValue,
        @NotBlank(message = "Result flag is required")
        String resultFlag
) {
}

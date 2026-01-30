package ifmo.se.coursach_back.medical.api.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateDonorStatusRequest(
        @NotBlank String donorStatus
) {
}

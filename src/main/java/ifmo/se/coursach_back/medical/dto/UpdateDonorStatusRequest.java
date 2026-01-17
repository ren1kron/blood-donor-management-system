package ifmo.se.coursach_back.medical.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateDonorStatusRequest(
        @NotBlank String donorStatus
) {
}

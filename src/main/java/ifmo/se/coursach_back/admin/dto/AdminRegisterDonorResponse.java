package ifmo.se.coursach_back.admin.dto;

import java.util.UUID;

public record AdminRegisterDonorResponse(
        UUID accountId,
        UUID donorId
) {
}

package ifmo.se.coursach_back.admin.api.dto;

import java.util.UUID;

public record AdminRegisterDonorResponse(
        UUID accountId,
        UUID donorId,
        String tempPassword
) {
}

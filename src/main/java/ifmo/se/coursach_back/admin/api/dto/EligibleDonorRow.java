package ifmo.se.coursach_back.admin.api.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record EligibleDonorRow(
        UUID donorId,
        String fullName,
        String phone,
        String email,
        OffsetDateTime lastDonationAt
) {
}

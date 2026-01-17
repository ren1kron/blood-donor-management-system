package ifmo.se.coursach_back.admin.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record EligibleDonorResponse(
        UUID donorId,
        String fullName,
        String phone,
        String email,
        OffsetDateTime lastDonationAt,
        long daysSinceDonation
) {
}

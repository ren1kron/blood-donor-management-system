package ifmo.se.coursach_back.admin.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record AdminDonorSummaryRow(
        UUID donorId,
        String fullName,
        String donorStatus,
        String email,
        String phone,
        OffsetDateTime lastDonationAt,
        OffsetDateTime lastAdmissionAt
) {
}

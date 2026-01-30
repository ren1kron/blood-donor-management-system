package ifmo.se.coursach_back.admin.api.dto;

import ifmo.se.coursach_back.donor.domain.DonorStatus;
import java.time.OffsetDateTime;
import java.util.UUID;

public record AdminDonorSummaryRow(
        UUID donorId,
        String fullName,
        DonorStatus donorStatus,
        String email,
        String phone,
        OffsetDateTime lastDonationAt,
        OffsetDateTime lastAdmissionAt
) {
}

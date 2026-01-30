package ifmo.se.coursach_back.admin.dto;

import ifmo.se.coursach_back.model.DonorStatus;
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

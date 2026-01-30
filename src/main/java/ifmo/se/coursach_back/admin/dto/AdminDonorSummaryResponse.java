package ifmo.se.coursach_back.admin.dto;

import ifmo.se.coursach_back.model.DonorStatus;
import java.time.OffsetDateTime;
import java.util.UUID;

public record AdminDonorSummaryResponse(
        UUID donorId,
        String fullName,
        DonorStatus donorStatus,
        String email,
        String phone,
        OffsetDateTime lastDonationAt,
        OffsetDateTime lastAdmissionAt
) {
    public static AdminDonorSummaryResponse from(AdminDonorSummaryRow row) {
        return new AdminDonorSummaryResponse(
                row.donorId(),
                row.fullName(),
                row.donorStatus(),
                row.email(),
                row.phone(),
                row.lastDonationAt(),
                row.lastAdmissionAt()
        );
    }
}

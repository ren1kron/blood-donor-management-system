package ifmo.se.coursach_back.admin.api.dto;

import ifmo.se.coursach_back.donor.application.ports.DonorSummary;
import ifmo.se.coursach_back.donor.domain.DonorStatus;
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
    /**
     * Creates response from DonorSummary port interface.
     */
    public static AdminDonorSummaryResponse from(DonorSummary summary) {
        DonorStatus status = null;
        if (summary.getDonorStatus() != null) {
            try {
                status = DonorStatus.valueOf(summary.getDonorStatus());
            } catch (IllegalArgumentException ignored) {
                // Keep as null if invalid
            }
        }
        
        return new AdminDonorSummaryResponse(
                summary.getDonorId(),
                summary.getFullName(),
                status,
                summary.getEmail(),
                summary.getPhone(),
                summary.getLastDonationAt(),
                summary.getLastAdmittedAt()
        );
    }

    /**
     * @deprecated Use {@link #from(DonorSummary)} instead
     */
    @Deprecated(forRemoval = true)
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

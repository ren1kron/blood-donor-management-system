package ifmo.se.coursach_back.admin.api.dto;

import ifmo.se.coursach_back.donor.domain.DonorStatus;
import ifmo.se.coursach_back.shared.infra.jpa.projection.DonorSummaryProjection;
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
     * Creates response from repository projection.
     */
    public static AdminDonorSummaryResponse from(DonorSummaryProjection projection) {
        DonorStatus status = null;
        if (projection.getDonorStatus() != null) {
            try {
                status = DonorStatus.valueOf(projection.getDonorStatus());
            } catch (IllegalArgumentException ignored) {
                // Keep as null if invalid
            }
        }
        
        return new AdminDonorSummaryResponse(
                projection.getDonorId(),
                projection.getFullName(),
                status,
                projection.getEmail(),
                projection.getPhone(),
                projection.getLastDonationAt(),
                projection.getLastAdmittedAt()
        );
    }

    /**
     * @deprecated Use {@link #from(DonorSummaryProjection)} instead
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

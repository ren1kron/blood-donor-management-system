package ifmo.se.coursach_back.staff.dto;

import ifmo.se.coursach_back.model.DonorStatus;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Summary row for staff donors list.
 */
public record StaffDonorSummary(
        UUID donorId,
        String fullName,
        String bloodGroup,
        String rhFactor,
        DonorStatus donorStatus,
        String email,
        String phone,
        OffsetDateTime lastDonationAt,
        Integer totalDonations
) {
}

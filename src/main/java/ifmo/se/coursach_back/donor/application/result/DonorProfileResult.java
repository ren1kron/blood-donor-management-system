package ifmo.se.coursach_back.donor.application.result;

import ifmo.se.coursach_back.donor.domain.DonorStatus;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Result for donor profile retrieval.
 */
public record DonorProfileResult(
        UUID accountId,
        UUID donorId,
        String fullName,
        LocalDate birthDate,
        String bloodGroup,
        String rhFactor,
        DonorStatus donorStatus,
        String email,
        String phone
) {
}

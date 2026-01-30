package ifmo.se.coursach_back.donor.api.dto;

import ifmo.se.coursach_back.donor.domain.DonorStatus;
import java.time.LocalDate;
import java.util.UUID;

public record DonorProfileResponse(
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

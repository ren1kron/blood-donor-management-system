package ifmo.se.coursach_back.donor.dto;

import java.time.LocalDate;
import java.util.UUID;

public record DonorProfileResponse(
        UUID accountId,
        UUID donorId,
        String fullName,
        LocalDate birthDate,
        String bloodGroup,
        String rhFactor,
        String donorStatus,
        String email,
        String phone
) {
}

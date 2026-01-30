package ifmo.se.coursach_back.donor.dto;

import ifmo.se.coursach_back.model.DonorStatus;
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

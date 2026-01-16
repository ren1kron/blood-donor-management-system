package ifmo.se.coursach_back.auth.dto;

import java.util.List;
import java.util.UUID;

public record AccountProfileResponse(
        UUID accountId,
        String email,
        String phone,
        List<String> roles,
        String profileType,
        String fullName
) {
}

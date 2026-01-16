package ifmo.se.coursach_back.auth.dto;

import java.util.List;
import java.util.UUID;

public record AuthResponse(
        String token,
        UUID accountId,
        List<String> roles
) {
}

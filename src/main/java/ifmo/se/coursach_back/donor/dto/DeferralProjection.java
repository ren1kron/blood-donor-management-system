package ifmo.se.coursach_back.donor.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public interface DeferralProjection {
    UUID getDeferralId();

    String getDeferralType();

    String getReason();

    OffsetDateTime getStartsAt();

    OffsetDateTime getEndsAt();
}

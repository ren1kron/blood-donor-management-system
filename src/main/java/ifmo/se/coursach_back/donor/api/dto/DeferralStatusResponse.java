package ifmo.se.coursach_back.donor.api.dto;

import ifmo.se.coursach_back.medical.domain.Deferral;
import ifmo.se.coursach_back.medical.domain.DeferralType;
import java.time.OffsetDateTime;
import java.util.UUID;

public record DeferralStatusResponse(
        UUID deferralId,
        DeferralType deferralType,
        String reason,
        OffsetDateTime startsAt,
        OffsetDateTime endsAt
) {
    public static DeferralStatusResponse from(Deferral deferral) {
        return new DeferralStatusResponse(
                deferral.getId(),
                deferral.getDeferralType(),
                deferral.getReason(),
                deferral.getStartsAt(),
                deferral.getEndsAt()
        );
    }
}

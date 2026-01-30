package ifmo.se.coursach_back.donor.dto;

import ifmo.se.coursach_back.model.Deferral;
import ifmo.se.coursach_back.model.DeferralType;
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

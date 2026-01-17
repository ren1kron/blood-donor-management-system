package ifmo.se.coursach_back.donor.dto;

import ifmo.se.coursach_back.model.Consent;
import java.time.OffsetDateTime;
import java.util.UUID;

public record ConsentResponse(
        UUID id,
        UUID visitId,
        UUID donorId,
        String consentType,
        OffsetDateTime givenAt
) {
    public static ConsentResponse from(Consent consent) {
        return new ConsentResponse(
                consent.getId(),
                consent.getVisit().getId(),
                consent.getDonor().getId(),
                consent.getConsentType(),
                consent.getGivenAt()
        );
    }
}

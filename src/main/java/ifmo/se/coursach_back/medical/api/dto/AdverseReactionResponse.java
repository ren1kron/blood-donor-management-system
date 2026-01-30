package ifmo.se.coursach_back.medical.api.dto;

import ifmo.se.coursach_back.medical.domain.AdverseReaction;
import java.time.OffsetDateTime;
import java.util.UUID;

public record AdverseReactionResponse(
        UUID id,
        UUID donationId,
        OffsetDateTime occurredAt,
        String severity,
        String description
) {
    public static AdverseReactionResponse from(AdverseReaction reaction) {
        return new AdverseReactionResponse(
                reaction.getId(),
                reaction.getDonation().getId(),
                reaction.getOccurredAt(),
                reaction.getSeverity() != null ? reaction.getSeverity().getValue() : null,
                reaction.getDescription()
        );
    }
}

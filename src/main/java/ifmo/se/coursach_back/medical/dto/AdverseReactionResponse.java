package ifmo.se.coursach_back.medical.dto;

import ifmo.se.coursach_back.model.AdverseReaction;
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
                reaction.getSeverity(),
                reaction.getDescription()
        );
    }
}

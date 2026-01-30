package ifmo.se.coursach_back.medical.application.result;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Result for scheduled donor in the medical queue.
 */
public record ScheduledDonorResult(
        UUID bookingId,
        UUID visitId,
        UUID donorId,
        String donorFullName,
        String donorStatus,
        UUID slotId,
        String slotPurpose,
        OffsetDateTime slotStartAt,
        OffsetDateTime slotEndAt,
        String slotLocation,
        String bookingStatus,
        boolean hasVisit,
        boolean hasMedicalCheck,
        String medicalCheckDecision,
        boolean hasDonation,
        boolean canDonate,
        UUID donationId,
        boolean donationPublished,
        boolean hasCollectionSession,
        UUID collectionSessionId,
        String collectionSessionStatus,
        OffsetDateTime collectionSessionStartedAt,
        OffsetDateTime collectionSessionEndedAt,
        String collectionSessionNurseName
) {
}

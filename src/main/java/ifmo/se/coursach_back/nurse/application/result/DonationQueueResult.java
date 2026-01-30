package ifmo.se.coursach_back.nurse.application.result;

import ifmo.se.coursach_back.appointment.domain.BookingStatus;
import ifmo.se.coursach_back.appointment.domain.SlotPurpose;
import ifmo.se.coursach_back.donor.domain.DonorStatus;
import ifmo.se.coursach_back.medical.domain.MedicalCheckDecision;
import ifmo.se.coursach_back.nurse.domain.CollectionSessionStatus;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Result object for listing the donation queue.
 */
public record DonationQueueResult(
        List<DonationQueueItem> items
) {
    /**
     * A single item in the donation queue representing a scheduled donor.
     */
    public record DonationQueueItem(
            UUID bookingId,
            UUID visitId,
            UUID donorId,
            String donorFullName,
            DonorStatus donorStatus,
            UUID slotId,
            SlotPurpose purpose,
            OffsetDateTime startAt,
            OffsetDateTime endAt,
            String location,
            BookingStatus bookingStatus,
            MedicalCheckDecision medicalDecision,
            boolean hasDonation,
            boolean canDonate,
            UUID donationId,
            boolean donationPublished,
            UUID collectionSessionId,
            CollectionSessionStatus collectionSessionStatus,
            OffsetDateTime collectionSessionStartedAt,
            OffsetDateTime collectionSessionEndedAt,
            String collectionSessionNurseName,
            String collectionSessionPreVitalsJson,
            String collectionSessionPostVitalsJson,
            String collectionSessionNotes,
            String collectionSessionComplications,
            String collectionSessionInterruptionReason
    ) {
    }
}

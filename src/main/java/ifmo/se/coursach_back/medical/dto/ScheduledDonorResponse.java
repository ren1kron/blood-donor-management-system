package ifmo.se.coursach_back.medical.dto;

import ifmo.se.coursach_back.model.Booking;
import ifmo.se.coursach_back.model.BookingStatus;
import ifmo.se.coursach_back.model.CollectionSession;
import ifmo.se.coursach_back.model.CollectionSessionStatus;
import ifmo.se.coursach_back.model.Donation;
import ifmo.se.coursach_back.model.DonorStatus;
import ifmo.se.coursach_back.model.MedicalCheck;
import ifmo.se.coursach_back.model.MedicalCheckDecision;
import ifmo.se.coursach_back.model.SlotPurpose;
import ifmo.se.coursach_back.model.Visit;
import java.time.OffsetDateTime;
import java.util.UUID;

public record ScheduledDonorResponse(
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
    public static ScheduledDonorResponse from(Booking booking, Visit visit, MedicalCheck check, Donation donation,
                                              CollectionSession session) {
        MedicalCheckDecision decision = check != null ? check.getDecision() : null;
        boolean hasDonation = donation != null;
        boolean hasSession = session != null;
        CollectionSessionStatus sessionStatus = session != null ? session.getStatus() : null;
        boolean sessionAllows = hasSession && sessionStatus != CollectionSessionStatus.ABORTED;
        boolean canDonate = decision == MedicalCheckDecision.ADMITTED && !hasDonation && sessionAllows;
        
        return new ScheduledDonorResponse(
                booking.getId(),
                visit != null ? visit.getId() : null,
                booking.getDonor().getId(),
                booking.getDonor().getFullName(),
                booking.getDonor().getDonorStatus(),
                booking.getSlot().getId(),
                booking.getSlot().getPurpose(),
                booking.getSlot().getStartAt(),
                booking.getSlot().getEndAt(),
                booking.getSlot().getLocation(),
                booking.getStatus(),
                decision,
                hasDonation,
                canDonate,
                donation != null ? donation.getId() : null,
                donation != null && donation.isPublished(),
                session != null ? session.getId() : null,
                sessionStatus,
                session != null ? session.getStartedAt() : null,
                session != null ? session.getEndedAt() : null,
                session != null && session.getNurse() != null ? session.getNurse().getFullName() : null,
                session != null ? session.getPreVitalsJson() : null,
                session != null ? session.getPostVitalsJson() : null,
                session != null ? session.getNotes() : null,
                session != null ? session.getComplications() : null,
                session != null ? session.getInterruptionReason() : null
        );
    }
    
    public static ScheduledDonorResponse from(Booking booking, Visit visit) {
        return from(booking, visit, null, null, null);
    }
}

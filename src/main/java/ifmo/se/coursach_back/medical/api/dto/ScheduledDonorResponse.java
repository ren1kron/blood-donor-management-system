package ifmo.se.coursach_back.medical.api.dto;

import ifmo.se.coursach_back.appointment.domain.Booking;
import ifmo.se.coursach_back.appointment.domain.BookingStatus;
import ifmo.se.coursach_back.nurse.api.dto.VitalsPayload;
import ifmo.se.coursach_back.nurse.domain.CollectionSession;
import ifmo.se.coursach_back.nurse.domain.CollectionSessionStatus;
import ifmo.se.coursach_back.medical.domain.Donation;
import ifmo.se.coursach_back.donor.domain.DonorStatus;
import ifmo.se.coursach_back.medical.domain.MedicalCheck;
import ifmo.se.coursach_back.medical.domain.MedicalCheckDecision;
import ifmo.se.coursach_back.appointment.domain.SlotPurpose;
import ifmo.se.coursach_back.appointment.domain.Visit;
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
        VitalsPayload collectionSessionPreVitals,
        VitalsPayload collectionSessionPostVitals,
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
                session != null ? vitalsFromSession(session, true) : null,
                session != null ? vitalsFromSession(session, false) : null,
                session != null ? session.getNotes() : null,
                session != null ? session.getComplications() : null,
                session != null ? session.getInterruptionReason() : null
        );
    }
    
    public static ScheduledDonorResponse from(Booking booking, Visit visit) {
        return from(booking, visit, null, null, null);
    }

    private static VitalsPayload vitalsFromSession(CollectionSession s, boolean pre) {
        if (pre) {
            if (s.getPreSystolicMmhg() == null && s.getPreDiastolicMmhg() == null
                    && s.getPrePulseRate() == null && s.getPreBodyTemperatureC() == null && s.getPreWellbeing() == null) {
                return null;
            }
            return new VitalsPayload(s.getPreSystolicMmhg(), s.getPreDiastolicMmhg(), s.getPrePulseRate(),
                    s.getPreBodyTemperatureC() != null ? s.getPreBodyTemperatureC().doubleValue() : null, s.getPreWellbeing());
        } else {
            if (s.getPostSystolicMmhg() == null && s.getPostDiastolicMmhg() == null
                    && s.getPostPulseRate() == null && s.getPostBodyTemperatureC() == null && s.getPostWellbeing() == null) {
                return null;
            }
            return new VitalsPayload(s.getPostSystolicMmhg(), s.getPostDiastolicMmhg(), s.getPostPulseRate(),
                    s.getPostBodyTemperatureC() != null ? s.getPostBodyTemperatureC().doubleValue() : null, s.getPostWellbeing());
        }
    }
}

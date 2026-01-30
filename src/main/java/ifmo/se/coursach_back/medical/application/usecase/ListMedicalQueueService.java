package ifmo.se.coursach_back.medical.application.usecase;

import ifmo.se.coursach_back.appointment.domain.Booking;
import ifmo.se.coursach_back.appointment.domain.Visit;
import ifmo.se.coursach_back.medical.application.MedicalWorkflowService;
import ifmo.se.coursach_back.medical.application.result.ScheduledDonorResult;
import ifmo.se.coursach_back.medical.domain.Donation;
import ifmo.se.coursach_back.medical.domain.MedicalCheck;
import ifmo.se.coursach_back.nurse.domain.CollectionSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ListMedicalQueueService implements ListMedicalQueueUseCase {
    private final MedicalWorkflowService medicalWorkflowService;

    @Override
    public List<ScheduledDonorResult> execute(OffsetDateTime from) {
        List<Booking> bookings = medicalWorkflowService.listScheduledBookings(from);
        
        if (bookings.isEmpty()) {
            return List.of();
        }
        
        List<UUID> bookingIds = bookings.stream()
                .map(Booking::getId)
                .toList();
        
        Map<UUID, Visit> visitsByBookingId = medicalWorkflowService.loadVisitsByBookingIds(bookingIds);
        
        List<UUID> visitIds = visitsByBookingId.values().stream()
                .map(Visit::getId)
                .toList();
        
        Map<UUID, MedicalCheck> checksByVisitId = medicalWorkflowService.loadMedicalChecksByVisitIds(visitIds);
        Map<UUID, Donation> donationsByVisitId = medicalWorkflowService.loadDonationsByVisitIds(visitIds);
        Map<UUID, CollectionSession> sessionsByVisitId = medicalWorkflowService.loadCollectionSessionsByVisitIds(visitIds);
        
        return bookings.stream()
                .map(booking -> {
                    Visit visit = visitsByBookingId.get(booking.getId());
                    MedicalCheck check = visit != null ? checksByVisitId.get(visit.getId()) : null;
                    Donation donation = visit != null ? donationsByVisitId.get(visit.getId()) : null;
                    CollectionSession session = visit != null ? sessionsByVisitId.get(visit.getId()) : null;
                    
                    // canDonate: collection session completed + no donation yet
                    // Medical check is optional - if session is completed, donation can be registered
                    boolean canDonate = session != null 
                            && "COMPLETED".equals(session.getStatus().name())
                            && donation == null;
                    
                    return new ScheduledDonorResult(
                            booking.getId(),
                            visit != null ? visit.getId() : null,
                            booking.getDonor().getId(),
                            booking.getDonor().getFullName(),
                            booking.getDonor().getDonorStatus() != null ? booking.getDonor().getDonorStatus().name() : null,
                            booking.getSlot().getId(),
                            booking.getSlot().getPurpose().name(),
                            booking.getSlot().getStartAt(),
                            booking.getSlot().getEndAt(),
                            booking.getSlot().getLocation(),
                            booking.getStatus().name(),
                            visit != null,
                            check != null,
                            check != null ? check.getDecision().name() : null,
                            donation != null,
                            canDonate,
                            donation != null ? donation.getId() : null,
                            donation != null && donation.isPublished(),
                            session != null,
                            session != null ? session.getId() : null,
                            session != null ? session.getStatus().name() : null,
                            session != null ? session.getStartedAt() : null,
                            session != null ? session.getEndedAt() : null,
                            session != null && session.getNurse() != null ? session.getNurse().getFullName() : null
                    );
                })
                .toList();
    }
}

package ifmo.se.coursach_back.medical.application;

import ifmo.se.coursach_back.shared.application.EntityResolverService;
import ifmo.se.coursach_back.exception.ConflictException;
import ifmo.se.coursach_back.exception.NotFoundException;
import ifmo.se.coursach_back.appointment.domain.Booking;
import ifmo.se.coursach_back.appointment.domain.BookingStatus;
import ifmo.se.coursach_back.lab.domain.LabExaminationRequest;
import ifmo.se.coursach_back.lab.domain.LabExaminationStatus;
import ifmo.se.coursach_back.appointment.domain.SlotPurpose;
import ifmo.se.coursach_back.admin.domain.StaffProfile;
import ifmo.se.coursach_back.appointment.domain.Visit;
import ifmo.se.coursach_back.lab.infra.jpa.LabExaminationRequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Service for lab examination request operations.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LabExaminationService {

    private final LabExaminationRequestRepository labExaminationRequestRepository;
    private final EntityResolverService entityResolver;

    /**
     * Creates a lab examination request for a visit.
     *
     * @param accountId the staff account ID
     * @param visitId   the visit ID
     * @return created or existing lab examination request
     */
    @Transactional
    public LabExaminationRequest createLabRequest(UUID accountId, UUID visitId) {
        StaffProfile doctor = entityResolver.requireStaff(accountId);
        Visit visit = entityResolver.getVisit(visitId);
        Booking booking = visit.getBooking();

        validateExaminationPurpose(booking);
        validateBookingConfirmed(booking);

        // Return existing if already created
        LabExaminationRequest existing = labExaminationRequestRepository.findByVisit_Id(visitId)
                .orElse(null);
        if (existing != null) {
            log.debug("Lab request already exists for visit: {}", visitId);
            return existing;
        }

        LabExaminationRequest request = new LabExaminationRequest();
        request.setVisit(visit);
        request.setRequestedBy(doctor);
        request.setRequestedAt(OffsetDateTime.now());
        request.setStatus(LabExaminationStatus.REQUESTED);

        LabExaminationRequest saved = labExaminationRequestRepository.save(request);
        log.info("Lab request created: requestId={}, visitId={}", saved.getId(), visitId);

        return saved;
    }

    /**
     * Gets a lab examination request by visit ID.
     */
    public LabExaminationRequest getByVisit(UUID visitId) {
        return labExaminationRequestRepository.findByVisit_Id(visitId)
                .orElseThrow(() -> NotFoundException.entity("Lab examination request", "visit " + visitId));
    }

    /**
     * Checks if lab examination is completed for a visit.
     */
    public boolean isCompleted(UUID visitId) {
        return labExaminationRequestRepository.findByVisit_Id(visitId)
                .map(r -> r.getStatus() == LabExaminationStatus.COMPLETED)
                .orElse(false);
    }

    private void validateExaminationPurpose(Booking booking) {
        if (booking.getSlot().getPurpose() != SlotPurpose.EXAMINATION) {
            throw new ConflictException("Visit is not scheduled for examination");
        }
    }

    private void validateBookingConfirmed(Booking booking) {
        if (!BookingStatus.CONFIRMED.equals(booking.getStatus())) {
            throw new ConflictException("Booking is not confirmed");
        }
    }
}

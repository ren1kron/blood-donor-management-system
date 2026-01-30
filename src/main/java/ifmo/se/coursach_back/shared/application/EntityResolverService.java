package ifmo.se.coursach_back.shared.application;

import ifmo.se.coursach_back.exception.BadRequestException;
import ifmo.se.coursach_back.exception.ConflictException;
import ifmo.se.coursach_back.exception.NotFoundException;
import ifmo.se.coursach_back.appointment.domain.Booking;
import ifmo.se.coursach_back.appointment.domain.BookingStatus;
import ifmo.se.coursach_back.admin.domain.StaffProfile;
import ifmo.se.coursach_back.appointment.domain.Visit;
import ifmo.se.coursach_back.appointment.application.ports.BookingRepositoryPort;
import ifmo.se.coursach_back.admin.application.ports.StaffProfileRepositoryPort;
import ifmo.se.coursach_back.appointment.application.ports.VisitRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Shared service for resolving visits, staff profiles, and other common entities.
 * Eliminates duplication across workflow services.
 */
@Service
@RequiredArgsConstructor
public class EntityResolverService {

    private final VisitRepositoryPort visitRepository;
    private final BookingRepositoryPort bookingRepository;
    private final StaffProfileRepositoryPort staffProfileRepository;

    /**
     * Resolves a visit by visitId or bookingId.
     * If booking doesn't have a visit, creates one.
     *
     * @param bookingId optional booking ID
     * @param visitId   optional visit ID
     * @return resolved visit
     * @throws NotFoundException   if neither booking nor visit found
     * @throws BadRequestException if both are null
     */
    @Transactional
    public Visit resolveVisit(UUID bookingId, UUID visitId) {
        if (visitId != null) {
            return visitRepository.findById(visitId)
                    .orElseThrow(() -> NotFoundException.entity("Visit", visitId));
        }

        if (bookingId == null) {
            throw new BadRequestException("Either bookingId or visitId must be provided");
        }

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> NotFoundException.entity("Booking", bookingId));

        if (BookingStatus.CANCELLED.equals(booking.getStatus())) {
            throw ConflictException.alreadyInState("Booking", "CANCELLED");
        }

        return visitRepository.findByBookingId(bookingId)
                .orElseGet(() -> createVisit(booking));
    }

    /**
     * Resolves a visit by booking ID only.
     *
     * @param bookingId booking ID
     * @return resolved visit
     */
    @Transactional
    public Visit resolveVisitByBooking(UUID bookingId) {
        return resolveVisit(bookingId, null);
    }

    /**
     * Gets an existing visit, does not create one.
     *
     * @param visitId visit ID
     * @return the visit
     * @throws NotFoundException if not found
     */
    public Visit getVisit(UUID visitId) {
        return visitRepository.findById(visitId)
                .orElseThrow(() -> NotFoundException.entity("Visit", visitId));
    }

    /**
     * Gets a staff profile by account ID.
     *
     * @param accountId account ID
     * @return staff profile
     * @throws NotFoundException if not found
     */
    public StaffProfile requireStaff(UUID accountId) {
        return staffProfileRepository.findByAccountId(accountId)
                .orElseThrow(() -> NotFoundException.entity("Staff profile", "account " + accountId));
    }

    /**
     * Gets a booking by ID.
     *
     * @param bookingId booking ID
     * @return the booking
     * @throws NotFoundException if not found
     */
    public Booking getBooking(UUID bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> NotFoundException.entity("Booking", bookingId));
    }

    private Visit createVisit(Booking booking) {
        Visit visit = new Visit();
        visit.setBooking(booking);
        return visitRepository.save(visit);
    }
}

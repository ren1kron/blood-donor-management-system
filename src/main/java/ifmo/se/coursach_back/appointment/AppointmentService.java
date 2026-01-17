package ifmo.se.coursach_back.appointment;

import ifmo.se.coursach_back.appointment.dto.CreateSlotRequest;
import ifmo.se.coursach_back.model.AppointmentSlot;
import ifmo.se.coursach_back.model.Booking;
import ifmo.se.coursach_back.model.DonorProfile;
import ifmo.se.coursach_back.repository.AppointmentSlotRepository;
import ifmo.se.coursach_back.repository.BookingRepository;
import ifmo.se.coursach_back.repository.DonorProfileRepository;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AppointmentService {
    private final AppointmentSlotRepository slotRepository;
    private final BookingRepository bookingRepository;
    private final DonorProfileRepository donorProfileRepository;

    public List<AppointmentSlot> listUpcomingSlots(OffsetDateTime from, String purpose) {
        if (purpose == null || purpose.isBlank()) {
            return slotRepository.findByStartAtAfterOrderByStartAtAsc(from);
        }
        return slotRepository.findByPurposeIgnoreCaseAndStartAtAfterOrderByStartAtAsc(purpose.trim(), from);
    }

    public AppointmentSlot createSlot(CreateSlotRequest request) {
        if (!request.endAt().isAfter(request.startAt())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "endAt must be after startAt");
        }

        AppointmentSlot slot = new AppointmentSlot();
        slot.setPurpose(request.purpose());
        slot.setStartAt(request.startAt());
        slot.setEndAt(request.endAt());
        slot.setLocation(request.location());
        slot.setCapacity(request.capacity());
        return slotRepository.save(slot);
    }

    public Booking createBooking(UUID accountId, UUID slotId) {
        DonorProfile donor = donorProfileRepository.findByAccountId(accountId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Donor profile not found"));
        AppointmentSlot slot = slotRepository.findById(slotId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Slot not found"));

        ensureSlotAvailable(donor, slot);

        Booking booking = new Booking();
        booking.setDonor(donor);
        booking.setSlot(slot);
        booking.setStatus("BOOKED");
        return bookingRepository.save(booking);
    }

    public List<Booking> listDonorBookings(UUID accountId) {
        return bookingRepository.findByDonor_Account_IdOrderBySlot_StartAtDesc(accountId);
    }

    @Transactional
    public Booking cancelBooking(UUID accountId, UUID bookingId) {
        Booking booking = bookingRepository.findByIdAndDonor_Account_Id(bookingId, accountId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found"));
        if (!"BOOKED".equalsIgnoreCase(booking.getStatus())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Only booked appointments can be cancelled");
        }
        booking.setStatus("CANCELLED");
        booking.setCancelledAt(OffsetDateTime.now());
        return bookingRepository.save(booking);
    }

    @Transactional
    public Booking rescheduleBooking(UUID accountId, UUID bookingId, UUID newSlotId) {
        Booking booking = bookingRepository.findByIdAndDonor_Account_Id(bookingId, accountId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found"));
        if (!"BOOKED".equalsIgnoreCase(booking.getStatus())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Only booked appointments can be rescheduled");
        }
        if (booking.getSlot().getId().equals(newSlotId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Booking is already for this slot");
        }

        AppointmentSlot newSlot = slotRepository.findById(newSlotId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Slot not found"));
        DonorProfile donor = booking.getDonor();
        ensureSlotAvailable(donor, newSlot);

        booking.setStatus("CANCELLED");
        booking.setCancelledAt(OffsetDateTime.now());
        bookingRepository.save(booking);

        Booking newBooking = new Booking();
        newBooking.setDonor(donor);
        newBooking.setSlot(newSlot);
        newBooking.setStatus("BOOKED");
        return bookingRepository.save(newBooking);
    }

    private void ensureSlotAvailable(DonorProfile donor, AppointmentSlot slot) {
        if (bookingRepository.existsByDonor_IdAndSlot_Id(donor.getId(), slot.getId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Booking already exists for this slot");
        }

        long bookedCount = bookingRepository.countBySlot_IdAndStatus(slot.getId(), "BOOKED");
        if (bookedCount >= slot.getCapacity()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Slot capacity exceeded");
        }
    }
}

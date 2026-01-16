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
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AppointmentService {
    private final AppointmentSlotRepository slotRepository;
    private final BookingRepository bookingRepository;
    private final DonorProfileRepository donorProfileRepository;

    public List<AppointmentSlot> listUpcomingSlots(OffsetDateTime from) {
        return slotRepository.findByStartAtAfterOrderByStartAtAsc(from);
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

        if (bookingRepository.existsByDonor_IdAndSlot_Id(donor.getId(), slotId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Booking already exists for this slot");
        }

        long bookedCount = bookingRepository.countBySlot_IdAndStatus(slotId, "BOOKED");
        if (bookedCount >= slot.getCapacity()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Slot capacity exceeded");
        }

        Booking booking = new Booking();
        booking.setDonor(donor);
        booking.setSlot(slot);
        booking.setStatus("BOOKED");
        return bookingRepository.save(booking);
    }
}

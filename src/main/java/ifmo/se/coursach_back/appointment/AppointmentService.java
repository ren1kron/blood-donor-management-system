package ifmo.se.coursach_back.appointment;

import ifmo.se.coursach_back.appointment.dto.CreateSlotRequest;
import ifmo.se.coursach_back.model.AppointmentSlot;
import ifmo.se.coursach_back.model.Booking;
import ifmo.se.coursach_back.model.DonorProfile;
import ifmo.se.coursach_back.model.MedicalCheck;
import ifmo.se.coursach_back.model.SlotPurpose;
import ifmo.se.coursach_back.repository.AppointmentSlotRepository;
import ifmo.se.coursach_back.repository.BookingRepository;
import ifmo.se.coursach_back.repository.DeferralRepository;
import ifmo.se.coursach_back.repository.DonorProfileRepository;
import ifmo.se.coursach_back.repository.MedicalCheckRepository;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Slf4j
public class AppointmentService {
    private static final int MEDICAL_CHECK_VALIDITY_MONTHS = 6;
    
    private final AppointmentSlotRepository slotRepository;
    private final BookingRepository bookingRepository;
    private final DonorProfileRepository donorProfileRepository;
    private final MedicalCheckRepository medicalCheckRepository;
    private final DeferralRepository deferralRepository;

    public List<AppointmentSlot> listUpcomingSlots(OffsetDateTime from, String purpose) {
        if (purpose == null || purpose.isBlank()) {
            return slotRepository.findByStartAtAfterOrderByStartAtAsc(from);
        }
        return slotRepository.findByPurposeIgnoreCaseAndStartAtAfterOrderByStartAtAsc(purpose.trim(), from);
    }

    public long getSlotBookedCount(UUID slotId) {
        return bookingRepository.countBySlot_IdAndStatus(slotId, "BOOKED");
    }

    public AppointmentSlot createSlot(CreateSlotRequest request) {
        log.info("Creating slot with purpose={}, startAt={}, endAt={}, location={}, capacity={}", 
                 request.purpose(), request.startAt(), request.endAt(), request.location(), request.capacity());
        
        if (!request.endAt().isAfter(request.startAt())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "endAt must be after startAt");
        }

        AppointmentSlot slot = new AppointmentSlot();
        slot.setPurpose(request.purpose());
        slot.setStartAt(request.startAt());
        slot.setEndAt(request.endAt());
        slot.setLocation(request.location());
        slot.setCapacity(request.capacity());
        
        AppointmentSlot saved = slotRepository.save(slot);
        log.info("Slot created successfully with id={}", saved.getId());
        return saved;
    }

    public Booking createBooking(UUID accountId, UUID slotId) {
        DonorProfile donor = donorProfileRepository.findByAccountId(accountId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Donor profile not found"));
        AppointmentSlot slot = slotRepository.findById(slotId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Slot not found"));

        ensureSlotAvailable(donor, slot);

        if (!SlotPurpose.DONATION.equalsIgnoreCase(slot.getPurpose())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Only donation slots can be booked via this endpoint.");
        }

        ensureDonorHasValidMedicalCheck(donor);

        Booking booking = new Booking();
        booking.setDonor(donor);
        booking.setSlot(slot);
        booking.setStatus("BOOKED");
        return bookingRepository.save(booking);
    }
    
    private void ensureDonorHasValidMedicalCheck(DonorProfile donor) {
        OffsetDateTime now = OffsetDateTime.now();
        var activeDeferral = deferralRepository.findActiveDeferral(donor.getId(), now).orElse(null);
        if (activeDeferral != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Невозможно записаться на донацию при активном отводе.");
        }

        MedicalCheck lastCheck = medicalCheckRepository
                .findTopByVisit_Booking_Donor_IdOrderByDecisionAtDesc(donor.getId())
                .orElse(null);
        if (lastCheck == null || !"ADMITTED".equalsIgnoreCase(lastCheck.getDecision())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Для записи на донацию необходимо пройти обследование и получить допуск врача.");
        }

        OffsetDateTime sixMonthsAgo = now.minusMonths(MEDICAL_CHECK_VALIDITY_MONTHS);
        if (lastCheck.getDecisionAt() != null && lastCheck.getDecisionAt().isBefore(sixMonthsAgo)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Срок действия обследования истек. Запишитесь на новое обследование.");
        }
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

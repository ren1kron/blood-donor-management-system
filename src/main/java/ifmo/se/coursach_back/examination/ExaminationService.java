package ifmo.se.coursach_back.examination;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ifmo.se.coursach_back.examination.dto.ConfirmExaminationRequest;
import ifmo.se.coursach_back.examination.dto.ConfirmExaminationResponse;
import ifmo.se.coursach_back.examination.dto.ExaminationBookingResponse;
import ifmo.se.coursach_back.examination.dto.ExaminationSlotResponse;
import ifmo.se.coursach_back.exception.BadRequestException;
import ifmo.se.coursach_back.exception.ConflictException;
import ifmo.se.coursach_back.exception.NotFoundException;
import ifmo.se.coursach_back.model.AppointmentSlot;
import ifmo.se.coursach_back.model.Booking;
import ifmo.se.coursach_back.model.BookingStatus;
import ifmo.se.coursach_back.model.Consent;
import ifmo.se.coursach_back.model.DonorProfile;
import ifmo.se.coursach_back.model.Questionnaire;
import ifmo.se.coursach_back.model.SlotPurpose;
import ifmo.se.coursach_back.model.Visit;
import ifmo.se.coursach_back.repository.AppointmentSlotRepository;
import ifmo.se.coursach_back.repository.BookingRepository;
import ifmo.se.coursach_back.repository.ConsentRepository;
import ifmo.se.coursach_back.repository.DonorProfileRepository;
import ifmo.se.coursach_back.repository.QuestionnaireRepository;
import ifmo.se.coursach_back.repository.VisitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ExaminationService {
    
    private final AppointmentSlotRepository slotRepository;
    private final BookingRepository bookingRepository;
    private final DonorProfileRepository donorProfileRepository;
    private final VisitRepository visitRepository;
    private final ConsentRepository consentRepository;
    private final QuestionnaireRepository questionnaireRepository;
    private final ObjectMapper objectMapper;
    
    public List<ExaminationSlotResponse> listAvailableSlots(OffsetDateTime from, OffsetDateTime to) {
        OffsetDateTime start = from != null ? from : OffsetDateTime.now();
        OffsetDateTime end = to != null ? to : start.plusDays(30);
        
        List<AppointmentSlot> slots = slotRepository
                .findByPurposeAndStartAtBetweenOrderByStartAtAsc(SlotPurpose.EXAMINATION, start, end);
        
        return slots.stream()
                .map(slot -> {
                    long activeBookings = bookingRepository.countActiveBookingsBySlotId(slot.getId());
                    return ExaminationSlotResponse.from(slot, activeBookings);
                })
                .filter(slot -> slot.remainingCapacity() > 0)
                .toList();
    }
    
    @Transactional
    public ExaminationBookingResponse createPendingBooking(UUID accountId, UUID slotId) {
        DonorProfile donor = requireDonor(accountId);
        AppointmentSlot slot = slotRepository.findById(slotId)
                .orElseThrow(() -> new NotFoundException("Slot not found"));
        
        if (slot.getPurpose() != SlotPurpose.EXAMINATION) {
            throw new BadRequestException("Slot is not for examination");
        }
        
        if (slot.getStartAt().isBefore(OffsetDateTime.now())) {
            throw new BadRequestException("Slot has already started");
        }
        
        var existingPending = bookingRepository.findByDonor_IdAndSlot_IdAndStatusAndCancelledAtIsNull(
                donor.getId(), slotId, BookingStatus.PENDING_QUESTIONNAIRE);
        if (existingPending.isPresent()) {
            return ExaminationBookingResponse.from(existingPending.get());
        }
        
        long activeBookings = bookingRepository.countActiveBookingsBySlotId(slotId);
        if (activeBookings >= slot.getCapacity()) {
            throw new ConflictException("No available capacity for this slot");
        }
        
        Booking booking = new Booking();
        booking.setDonor(donor);
        booking.setSlot(slot);
        booking.setStatus(BookingStatus.PENDING_QUESTIONNAIRE);
        booking.setCreatedAt(OffsetDateTime.now());
        
        Booking saved = bookingRepository.save(booking);
        return ExaminationBookingResponse.from(saved);
    }
    
    @Transactional
    public ConfirmExaminationResponse confirmBooking(UUID accountId, UUID bookingId, 
                                                      ConfirmExaminationRequest request) {
        DonorProfile donor = requireDonor(accountId);
        
        Booking booking = bookingRepository.findByIdAndDonor_Id(bookingId, donor.getId())
                .orElseThrow(() -> new NotFoundException("Booking not found"));
        
        if (!BookingStatus.PENDING_QUESTIONNAIRE.equals(booking.getStatus())) {
            throw new BadRequestException("Booking is not in pending state. Current status: " + booking.getStatus());
        }
        
        if (request.consentGiven() == null || !request.consentGiven()) {
            throw new BadRequestException("Consent must be given to confirm booking");
        }
        
        AppointmentSlot slot = booking.getSlot();
        long activeBookings = bookingRepository.countActiveBookingsBySlotId(slot.getId());
        long confirmedCount = activeBookings - 1; // This booking is pending, others are confirmed/booked
        if (confirmedCount >= slot.getCapacity()) {
            throw new ConflictException("Slot capacity exceeded");
        }
        
        OffsetDateTime now = OffsetDateTime.now();
        
        Visit visit = new Visit();
        visit.setBooking(booking);
        visit.setVisitStatus("SCHEDULED");
        Visit savedVisit = visitRepository.save(visit);
        
        Consent consent = new Consent();
        consent.setVisit(savedVisit);
        consent.setDonor(donor);
        consent.setConsentType(request.consentType());
        consent.setGivenAt(now);
        consentRepository.save(consent);
        
        Questionnaire questionnaire = new Questionnaire();
        questionnaire.setVisit(savedVisit);
        questionnaire.setDonor(donor);
        questionnaire.setFilledAt(now);
        questionnaire.setPayloadJson(serializePayload(request.questionnairePayload()));
        questionnaireRepository.save(questionnaire);
        
        booking.setStatus(BookingStatus.CONFIRMED);
        bookingRepository.save(booking);
        
        return new ConfirmExaminationResponse(
                booking.getId(),
                savedVisit.getId(),
                booking.getStatus(),
                slot.getStartAt(),
                slot.getEndAt(),
                slot.getLocation()
        );
    }
    
    @Transactional
    public void cancelBooking(UUID accountId, UUID bookingId) {
        DonorProfile donor = requireDonor(accountId);
        
        Booking booking = bookingRepository.findByIdAndDonor_Id(bookingId, donor.getId())
                .orElseThrow(() -> new NotFoundException("Booking not found"));
        
        if (BookingStatus.CANCELLED.equals(booking.getStatus())) {
            return;
        }
        
        if (!BookingStatus.PENDING_QUESTIONNAIRE.equals(booking.getStatus()) 
                && !BookingStatus.CONFIRMED.equals(booking.getStatus())) {
            throw new BadRequestException("Cannot cancel booking with status: " + booking.getStatus());
        }
        
        booking.setStatus(BookingStatus.CANCELLED);
        booking.setCancelledAt(OffsetDateTime.now());
        bookingRepository.save(booking);
    }
    
    public ExaminationBookingResponse getBooking(UUID accountId, UUID bookingId) {
        DonorProfile donor = requireDonor(accountId);
        
        Booking booking = bookingRepository.findByIdAndDonor_Id(bookingId, donor.getId())
                .orElseThrow(() -> new NotFoundException("Booking not found"));
        
        return ExaminationBookingResponse.from(booking);
    }
    
    private DonorProfile requireDonor(UUID accountId) {
        return donorProfileRepository.findByAccountId(accountId)
                .orElseThrow(() -> new NotFoundException("Donor profile not found"));
    }
    
    private String serializePayload(Object payload) {
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            throw new BadRequestException("Failed to serialize questionnaire payload");
        }
    }
}

package ifmo.se.coursach_back.medical;

import ifmo.se.coursach_back.medical.dto.AdverseReactionRequest;
import ifmo.se.coursach_back.medical.dto.DeferralRequest;
import ifmo.se.coursach_back.medical.dto.DonationRequest;
import ifmo.se.coursach_back.medical.dto.MedicalCheckRequest;
import ifmo.se.coursach_back.medical.dto.SampleRequest;
import ifmo.se.coursach_back.model.AdverseReaction;
import ifmo.se.coursach_back.model.Booking;
import ifmo.se.coursach_back.model.BookingStatus;
import ifmo.se.coursach_back.model.Deferral;
import ifmo.se.coursach_back.model.Donation;
import ifmo.se.coursach_back.model.DonorProfile;
import ifmo.se.coursach_back.model.MedicalCheck;
import ifmo.se.coursach_back.model.Sample;
import ifmo.se.coursach_back.model.StaffProfile;
import ifmo.se.coursach_back.model.Visit;
import ifmo.se.coursach_back.repository.AdverseReactionRepository;
import ifmo.se.coursach_back.repository.BookingRepository;
import ifmo.se.coursach_back.repository.DeferralRepository;
import ifmo.se.coursach_back.repository.DonationRepository;
import ifmo.se.coursach_back.repository.DonorProfileRepository;
import ifmo.se.coursach_back.repository.MedicalCheckRepository;
import ifmo.se.coursach_back.repository.SampleRepository;
import ifmo.se.coursach_back.repository.StaffProfileRepository;
import ifmo.se.coursach_back.repository.VisitRepository;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class MedicalWorkflowService {
    private final BookingRepository bookingRepository;
    private final VisitRepository visitRepository;
    private final MedicalCheckRepository medicalCheckRepository;
    private final DeferralRepository deferralRepository;
    private final DonationRepository donationRepository;
    private final SampleRepository sampleRepository;
    private final AdverseReactionRepository adverseReactionRepository;
    private final DonorProfileRepository donorProfileRepository;
    private final StaffProfileRepository staffProfileRepository;

    public List<Booking> listScheduledBookings(OffsetDateTime from) {
        return bookingRepository.findByStatusInAndSlot_StartAtAfterOrderBySlot_StartAtAsc(
                List.of(BookingStatus.BOOKED, BookingStatus.CONFIRMED), from);
    }

    public Map<UUID, Visit> loadVisitsByBookingIds(List<UUID> bookingIds) {
        if (bookingIds.isEmpty()) {
            return new HashMap<>();
        }
        return visitRepository.findByBooking_IdIn(bookingIds).stream()
                .collect(Collectors.toMap(visit -> visit.getBooking().getId(), visit -> visit));
    }

    public record MedicalCheckResult(MedicalCheck check, Deferral deferral) {
    }

    @Transactional
    public MedicalCheckResult recordMedicalCheck(UUID accountId, MedicalCheckRequest request) {
        StaffProfile staff = requireStaff(accountId);
        Visit visit = resolveVisit(request.bookingId(), request.visitId());

        String decision = normalizeDecision(request.decision());
        if (!decision.equals("ADMITTED") && !decision.equals("REFUSED")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Decision must be ADMITTED or REFUSED");
        }
        if (decision.equals("REFUSED") && request.deferral() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Deferral is required when decision is REFUSED");
        }
        if (decision.equals("ADMITTED") && request.deferral() != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Deferral is not allowed when decision is ADMITTED");
        }

        MedicalCheck check = medicalCheckRepository.findByVisit_Id(visit.getId()).orElseGet(MedicalCheck::new);
        check.setVisit(visit);
        check.setPerformedBy(staff);
        check.setWeightKg(request.weightKg());
        check.setHemoglobinGl(request.hemoglobinGl());
        check.setSystolicMmhg(request.systolicMmhg());
        check.setDiastolicMmhg(request.diastolicMmhg());
        check.setDecision(decision);
        check.setDecisionAt(OffsetDateTime.now());

        MedicalCheck saved = medicalCheckRepository.save(check);
        DeferralRequest deferralRequest = request.deferral();
        Deferral savedDeferral = null;
        if (deferralRequest != null) {
            validateDeferral(deferralRequest);
            Deferral deferral = new Deferral();
            deferral.setDonor(visit.getBooking().getDonor());
            deferral.setCreatedFromCheck(saved);
            deferral.setDeferralType(deferralRequest.deferralType());
            deferral.setReason(deferralRequest.reason());
            deferral.setEndsAt(deferralRequest.endsAt());
            savedDeferral = deferralRepository.save(deferral);
        }

        return new MedicalCheckResult(saved, savedDeferral);
    }

    @Transactional
    public Donation recordDonation(UUID accountId, DonationRequest request) {
        StaffProfile staff = requireStaff(accountId);
        Visit visit = resolveVisit(request.bookingId(), request.visitId());

        MedicalCheck check = medicalCheckRepository.findByVisit_Id(visit.getId()).orElse(null);
        if (check != null && "REFUSED".equalsIgnoreCase(check.getDecision())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Donation not allowed after refusal");
        }

        if (donationRepository.findByVisit_Id(visit.getId()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Donation already registered for this visit");
        }

        Donation donation = new Donation();
        donation.setVisit(visit);
        donation.setDonationType(request.donationType());
        donation.setVolumeMl(request.volumeMl());
        donation.setPerformedBy(staff);
        return donationRepository.save(donation);
    }

    @Transactional
    public Sample registerSample(SampleRequest request) {
        if (sampleRepository.existsBySampleCode(request.sampleCode())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Sample code already exists");
        }

        Donation donation = donationRepository.findById(request.donationId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Donation not found"));

        Sample sample = new Sample();
        sample.setDonation(donation);
        sample.setSampleCode(request.sampleCode());
        sample.setStatus(normalizeSampleStatus(request.status()));
        sample.setQuarantineReason(request.quarantineReason());
        sample.setRejectionReason(request.rejectionReason());
        return sampleRepository.save(sample);
    }

    @Transactional
    public AdverseReaction registerReaction(UUID accountId, AdverseReactionRequest request) {
        StaffProfile staff = requireStaff(accountId);
        Donation donation = donationRepository.findById(request.donationId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Donation not found"));

        AdverseReaction reaction = new AdverseReaction();
        reaction.setDonation(donation);
        reaction.setReportedBy(staff);
        reaction.setOccurredAt(request.occurredAt());
        reaction.setSeverity(request.severity());
        reaction.setDescription(request.description());
        return adverseReactionRepository.save(reaction);
    }

    @Transactional
    public DonorProfile updateDonorStatus(UUID donorId, String donorStatus) {
        DonorProfile donor = donorProfileRepository.findById(donorId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Donor not found"));
        donor.setDonorStatus(donorStatus);
        return donorProfileRepository.save(donor);
    }

    private Visit resolveVisit(UUID bookingId, UUID visitId) {
        if (visitId != null) {
            return visitRepository.findById(visitId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Visit not found"));
        }
        if (bookingId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "bookingId or visitId is required");
        }

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found"));
        if ("CANCELLED".equalsIgnoreCase(booking.getStatus())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Booking is cancelled");
        }

        return visitRepository.findByBooking_Id(bookingId)
                .orElseGet(() -> visitRepository.save(newVisit(booking)));
    }

    private Visit newVisit(Booking booking) {
        Visit visit = new Visit();
        visit.setBooking(booking);
        return visit;
    }

    private StaffProfile requireStaff(UUID accountId) {
        return staffProfileRepository.findByAccountId(accountId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Staff profile not found"));
    }

    private void validateDeferral(DeferralRequest request) {
        if (request.endsAt() != null && request.endsAt().isBefore(OffsetDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Deferral end time is in the past");
        }
    }

    private String normalizeDecision(String value) {
        String trimmed = value == null ? "" : value.trim();
        if (trimmed.isEmpty()) {
            return "";
        }
        return trimmed.toUpperCase();
    }

    private String normalizeSampleStatus(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed.toUpperCase();
    }
}

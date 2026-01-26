package ifmo.se.coursach_back.donor;

import ifmo.se.coursach_back.donor.dto.ConsentRequest;
import ifmo.se.coursach_back.donor.dto.DeferralStatusResponse;
import ifmo.se.coursach_back.donor.dto.DeferralProjection;
import ifmo.se.coursach_back.donor.dto.DonorProfileResponse;
import ifmo.se.coursach_back.donor.dto.DonationHistoryProjection;
import ifmo.se.coursach_back.donor.dto.EligibilityResponse;
import ifmo.se.coursach_back.donor.dto.LabResultProjection;
import ifmo.se.coursach_back.donor.dto.UpdateDonorProfileRequest;
import ifmo.se.coursach_back.model.Account;
import ifmo.se.coursach_back.model.Consent;
import ifmo.se.coursach_back.model.Donation;
import ifmo.se.coursach_back.model.DonorProfile;
import ifmo.se.coursach_back.model.NotificationDelivery;
import ifmo.se.coursach_back.model.Booking;
import ifmo.se.coursach_back.model.Visit;
import ifmo.se.coursach_back.repository.AccountRepository;
import ifmo.se.coursach_back.repository.BookingRepository;
import ifmo.se.coursach_back.repository.ConsentRepository;
import ifmo.se.coursach_back.repository.DeferralRepository;
import ifmo.se.coursach_back.repository.DonationRepository;
import ifmo.se.coursach_back.repository.DonorProfileRepository;
import ifmo.se.coursach_back.repository.LabTestResultRepository;
import ifmo.se.coursach_back.repository.NotificationDeliveryRepository;
import ifmo.se.coursach_back.repository.VisitRepository;
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
public class DonorService {
    private static final int REPEAT_DONATION_DAYS = 56;

    private final AccountRepository accountRepository;
    private final DonorProfileRepository donorProfileRepository;
    private final DonationRepository donationRepository;
    private final LabTestResultRepository labTestResultRepository;
    private final DeferralRepository deferralRepository;
    private final ConsentRepository consentRepository;
    private final VisitRepository visitRepository;
    private final NotificationDeliveryRepository notificationDeliveryRepository;
    private final BookingRepository bookingRepository;

    public DonorProfileResponse getProfile(UUID accountId) {
        DonorProfile donor = requireDonor(accountId);
        Account account = donor.getAccount();
        return new DonorProfileResponse(
                account.getId(),
                donor.getId(),
                account.getFullName(),
                donor.getBirthDate(),
                donor.getBloodGroup(),
                donor.getRhFactor(),
                donor.getDonorStatus(),
                account.getEmail(),
                account.getPhone()
        );
    }

    @Transactional
    public DonorProfileResponse updateProfile(UUID accountId, UpdateDonorProfileRequest request) {
        DonorProfile donor = requireDonor(accountId);
        Account account = donor.getAccount();

        if (request.fullName() != null) {
            String fullName = normalizeRequired(request.fullName(), "fullName");
            account.setFullName(fullName);
        }
        if (request.birthDate() != null) {
            donor.setBirthDate(request.birthDate());
        }
        if (request.bloodGroup() != null) {
            donor.setBloodGroup(normalizeNullable(request.bloodGroup()));
        }
        if (request.rhFactor() != null) {
            donor.setRhFactor(normalizeNullable(request.rhFactor()));
        }

        if (request.email() != null) {
            String email = normalizeNullable(request.email());
            if (email != null && accountRepository.existsByEmailIgnoreCaseAndIdNot(email, account.getId())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Email is already in use");
            }
            account.setEmail(email);
        }
        if (request.phone() != null) {
            String phone = normalizeNullable(request.phone());
            if (phone != null && accountRepository.existsByPhoneAndIdNot(phone, account.getId())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Phone is already in use");
            }
            account.setPhone(phone);
        }

        if (account.getEmail() == null && account.getPhone() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email or phone is required");
        }

        accountRepository.save(account);
        donorProfileRepository.save(donor);
        return getProfile(accountId);
    }

    @Transactional
    public Consent createConsent(UUID accountId, ConsentRequest request) {
        DonorProfile donor = requireDonor(accountId);
        Visit visit = resolveVisitForConsent(donor, request);

        Consent consent = new Consent();
        consent.setVisit(visit);
        consent.setDonor(donor);
        consent.setConsentType(normalizeRequired(request.consentType(), "consentType"));
        return consentRepository.save(consent);
    }

    public List<DonationHistoryProjection> listDonationHistory(UUID accountId) {
        requireDonor(accountId);
        return donationRepository.findDonorDonations(accountId);
    }

    public List<LabResultProjection> listPublishedResults(UUID accountId) {
        requireDonor(accountId);
        return labTestResultRepository.findPublishedByDonorAccountId(accountId);
    }

    public EligibilityResponse getEligibility(UUID accountId) {
        DonorProfile donor = requireDonor(accountId);
        OffsetDateTime now = OffsetDateTime.now();

        DeferralProjection activeDeferral = deferralRepository.findActiveDeferral(donor.getId(), now).orElse(null);
        Donation lastDonation = donationRepository
                .findTopByVisit_Booking_Donor_Account_IdOrderByPerformedAtDesc(accountId)
                .orElse(null);

        OffsetDateTime lastDonationAt = lastDonation != null ? lastDonation.getPerformedAt() : null;
        OffsetDateTime nextEligibleAt = lastDonationAt != null ? lastDonationAt.plusDays(REPEAT_DONATION_DAYS) : null;
        if (activeDeferral != null) {
            if (activeDeferral.getEndsAt() != null) {
                if (nextEligibleAt == null || activeDeferral.getEndsAt().isAfter(nextEligibleAt)) {
                    nextEligibleAt = activeDeferral.getEndsAt();
                }
            } else {
                nextEligibleAt = null;
            }
        }

        boolean activeStatus = "ACTIVE".equalsIgnoreCase(donor.getDonorStatus());
        boolean eligibleByDonation = nextEligibleAt == null || !now.isBefore(nextEligibleAt);
        boolean eligible = activeStatus && activeDeferral == null && eligibleByDonation;

        return new EligibilityResponse(
                donor.getDonorStatus(),
                eligible,
                lastDonationAt,
                nextEligibleAt,
                activeDeferral == null ? null : DeferralStatusResponse.fromProjection(activeDeferral)
        );
    }

    public List<NotificationDelivery> listNotifications(UUID accountId) {
        DonorProfile donor = requireDonor(accountId);
        return notificationDeliveryRepository.findByDonor_IdOrderBySentAtDesc(donor.getId());
    }

    @Transactional
    public void acknowledgeNotification(UUID accountId, UUID deliveryId) {
        DonorProfile donor = requireDonor(accountId);
        NotificationDelivery delivery = notificationDeliveryRepository.findByIdAndDonor_Id(deliveryId, donor.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Notification not found"));
        notificationDeliveryRepository.acknowledgeDelivery(delivery.getId(), donor.getId());
    }

    private DonorProfile requireDonor(UUID accountId) {
        return donorProfileRepository.findByAccountId(accountId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Donor profile not found"));
    }

    private Visit resolveVisitForConsent(DonorProfile donor, ConsentRequest request) {
        if (request.visitId() != null) {
            Visit visit = visitRepository.findById(request.visitId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Visit not found"));
            if (!visit.getBooking().getDonor().getId().equals(donor.getId())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Visit does not belong to donor");
            }
            return visit;
        }
        if (request.bookingId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "bookingId or visitId is required");
        }
        Booking booking = bookingRepository.findById(request.bookingId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found"));
        if (!booking.getDonor().getId().equals(donor.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Booking does not belong to donor");
        }
        if ("CANCELLED".equalsIgnoreCase(booking.getStatus())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Booking is cancelled");
        }
        return visitRepository.findByBooking_Id(booking.getId())
                .orElseGet(() -> visitRepository.save(newVisit(booking)));
    }

    private Visit newVisit(Booking booking) {
        Visit visit = new Visit();
        visit.setBooking(booking);
        return visit;
    }

    private String normalizeRequired(String value, String fieldName) {
        String trimmed = normalizeNullable(value);
        if (trimmed == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, fieldName + " is required");
        }
        return trimmed;
    }

    private String normalizeNullable(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}

package ifmo.se.coursach_back.donor.application;

import ifmo.se.coursach_back.donor.api.dto.ConsentRequest;
import ifmo.se.coursach_back.donor.api.dto.DeferralStatusResponse;
import ifmo.se.coursach_back.donor.api.dto.DonationHistoryResponse;
import ifmo.se.coursach_back.donor.api.dto.DonorProfileResponse;
import ifmo.se.coursach_back.donor.api.dto.EligibilityResponse;
import ifmo.se.coursach_back.donor.api.dto.UpdateDonorProfileRequest;
import ifmo.se.coursach_back.donor.domain.BloodGroup;
import ifmo.se.coursach_back.donor.domain.ConsentType;
import ifmo.se.coursach_back.donor.domain.RhFactor;
import ifmo.se.coursach_back.exception.BadRequestException;
import ifmo.se.coursach_back.exception.ConflictException;
import ifmo.se.coursach_back.exception.NotFoundException;
import ifmo.se.coursach_back.shared.domain.Account;
import ifmo.se.coursach_back.nurse.domain.CollectionSession;
import ifmo.se.coursach_back.donor.domain.Consent;
import ifmo.se.coursach_back.medical.domain.Deferral;
import ifmo.se.coursach_back.notification.domain.DeliveryStatus;
import ifmo.se.coursach_back.medical.domain.Donation;
import ifmo.se.coursach_back.donor.domain.DonorProfile;
import ifmo.se.coursach_back.donor.domain.DonorStatus;
import ifmo.se.coursach_back.lab.domain.LabTestResult;
import ifmo.se.coursach_back.medical.domain.MedicalCheck;
import ifmo.se.coursach_back.medical.domain.MedicalCheckDecision;
import ifmo.se.coursach_back.notification.domain.NotificationDelivery;
import ifmo.se.coursach_back.appointment.domain.Booking;
import ifmo.se.coursach_back.appointment.domain.BookingStatus;
import ifmo.se.coursach_back.appointment.domain.Visit;
import ifmo.se.coursach_back.shared.application.ports.AccountRepositoryPort;
import ifmo.se.coursach_back.appointment.application.ports.BookingRepositoryPort;
import ifmo.se.coursach_back.nurse.application.ports.CollectionSessionRepositoryPort;
import ifmo.se.coursach_back.donor.application.ports.ConsentRepositoryPort;
import ifmo.se.coursach_back.medical.application.ports.DeferralRepositoryPort;
import ifmo.se.coursach_back.medical.application.ports.DonationRepositoryPort;
import ifmo.se.coursach_back.donor.application.ports.DonorProfileRepositoryPort;
import ifmo.se.coursach_back.lab.application.ports.LabTestResultRepositoryPort;
import ifmo.se.coursach_back.medical.application.ports.MedicalCheckRepositoryPort;
import ifmo.se.coursach_back.notification.application.ports.NotificationDeliveryRepositoryPort;
import ifmo.se.coursach_back.appointment.application.ports.VisitRepositoryPort;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DonorService {
    private static final int REPEAT_DONATION_DAYS = 56;
    private static final int MEDICAL_CHECK_VALIDITY_MONTHS = 6;

    private final AccountRepositoryPort accountRepository;
    private final DonorProfileRepositoryPort donorProfileRepository;
    private final DonationRepositoryPort donationRepository;
    private final LabTestResultRepositoryPort labTestResultRepository;
    private final DeferralRepositoryPort deferralRepository;
    private final ConsentRepositoryPort consentRepository;
    private final VisitRepositoryPort visitRepository;
    private final NotificationDeliveryRepositoryPort notificationDeliveryRepository;
    private final BookingRepositoryPort bookingRepository;
    private final MedicalCheckRepositoryPort medicalCheckRepository;
    private final CollectionSessionRepositoryPort collectionSessionRepository;

    public DonorProfileResponse getProfile(UUID accountId) {
        DonorProfile donor = requireDonor(accountId);
        Account account = donor.getAccount();
        return new DonorProfileResponse(
                account.getId(),
                donor.getId(),
                donor.getFullName(),
                donor.getBirthDate(),
                donor.getBloodGroup() != null ? donor.getBloodGroup().getDisplayValue() : null,
                donor.getRhFactor() != null ? donor.getRhFactor().getDisplayValue() : null,
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
            donor.setFullName(fullName);
        }
        if (request.birthDate() != null) {
            donor.setBirthDate(request.birthDate());
        }
        if (request.bloodGroup() != null) {
            donor.setBloodGroup(BloodGroup.fromStringOrNull(request.bloodGroup()));
        }
        if (request.rhFactor() != null) {
            donor.setRhFactor(RhFactor.fromStringOrNull(request.rhFactor()));
        }

        if (request.email() != null) {
            String email = normalizeNullable(request.email());
            if (email != null && accountRepository.existsByEmailIgnoreCaseAndIdNot(email, account.getId())) {
                throw new ConflictException("Email is already in use");
            }
            account.setEmail(email);
        }
        if (request.phone() != null) {
            String phone = normalizeNullable(request.phone());
            if (phone != null && accountRepository.existsByPhoneAndIdNot(phone, account.getId())) {
                throw new ConflictException("Phone is already in use");
            }
            account.setPhone(phone);
        }

        if (account.getEmail() == null && account.getPhone() == null) {
            throw new BadRequestException("Email or phone is required");
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
        consent.setConsentType(ConsentType.fromString(request.consentType()));
        return consentRepository.save(consent);
    }

    public List<DonationHistoryResponse> listDonationHistory(UUID accountId) {
        requireDonor(accountId);
        List<Donation> donations = donationRepository.findPublishedByDonorAccountId(accountId);
        
        List<UUID> visitIds = donations.stream()
                .map(d -> d.getVisit().getId())
                .toList();
        
        java.util.Map<UUID, CollectionSession> sessionMap = collectionSessionRepository.findByVisitIds(visitIds)
                .stream()
                .collect(java.util.stream.Collectors.toMap(s -> s.getVisit().getId(), s -> s));
        
        return donations.stream()
                .map(d -> DonationHistoryResponse.from(d, sessionMap.get(d.getVisit().getId())))
                .toList();
    }

    public List<LabTestResult> listPublishedResults(UUID accountId) {
        requireDonor(accountId);
        return labTestResultRepository.findPublishedByDonorAccountId(accountId);
    }

    public List<MedicalCheck> listVisitHistory(UUID accountId) {
        DonorProfile donor = requireDonor(accountId);
        return medicalCheckRepository.findByDonorId(donor.getId());
    }

    public EligibilityResponse getEligibility(UUID accountId) {
        DonorProfile donor = requireDonor(accountId);
        OffsetDateTime now = OffsetDateTime.now();

        Deferral activeDeferral = deferralRepository.findActiveDeferral(donor.getId(), now).orElse(null);
        Donation lastDonation = donationRepository
                .findLatestByDonorAccountId(accountId)
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

        boolean activeStatus = (donor.getDonorStatus() == DonorStatus.ACTIVE)
                || (donor.getDonorStatus() == DonorStatus.POTENTIAL);
        boolean eligibleByDonation = nextEligibleAt == null || !now.isBefore(nextEligibleAt);
        boolean eligible = activeStatus && activeDeferral == null && eligibleByDonation;
        
        MedicalCheck latestCheck = medicalCheckRepository
                .findLatestByDonorId(donor.getId())
                .orElse(null);
        OffsetDateTime medicalCheckValidUntil = null;
        boolean hasValidMedicalCheck = false;
        if (latestCheck != null && latestCheck.getDecision() == MedicalCheckDecision.ADMITTED) {
            medicalCheckValidUntil = latestCheck.getDecisionAt().plusMonths(MEDICAL_CHECK_VALIDITY_MONTHS);
            hasValidMedicalCheck = !now.isAfter(medicalCheckValidUntil);
        }

        boolean canBookDonation = eligible && hasValidMedicalCheck;

        return new EligibilityResponse(
                donor.getDonorStatus(),
                eligible,
                canBookDonation,
                lastDonationAt,
                nextEligibleAt,
                medicalCheckValidUntil,
                activeDeferral == null ? null : DeferralStatusResponse.from(activeDeferral)
        );
    }

    public List<NotificationDelivery> listNotifications(UUID accountId) {
        DonorProfile donor = requireDonor(accountId);
        return notificationDeliveryRepository.findRecentByDonorId(donor.getId());
    }

    @Transactional
    public void acknowledgeNotification(UUID accountId, UUID deliveryId) {
        DonorProfile donor = requireDonor(accountId);
        NotificationDelivery delivery = notificationDeliveryRepository.findByIdAndDonorId(deliveryId, donor.getId())
                .orElseThrow(() -> new NotFoundException("Notification not found"));
        delivery.setStatus(DeliveryStatus.ACKED);
        notificationDeliveryRepository.save(delivery);
    }

    private DonorProfile requireDonor(UUID accountId) {
        return donorProfileRepository.findByAccountId(accountId)
                .orElseThrow(() -> new NotFoundException("Donor profile not found"));
    }

    private Visit resolveVisitForConsent(DonorProfile donor, ConsentRequest request) {
        if (request.visitId() != null) {
            Visit visit = visitRepository.findById(request.visitId())
                    .orElseThrow(() -> new NotFoundException("Visit not found"));
            if (!visit.getBooking().getDonor().getId().equals(donor.getId())) {
                throw new BadRequestException("Visit does not belong to donor");
            }
            return visit;
        }
        if (request.bookingId() == null) {
            throw new BadRequestException("bookingId or visitId is required");
        }
        Booking booking = bookingRepository.findById(request.bookingId())
                .orElseThrow(() -> new NotFoundException("Booking not found"));
        if (!booking.getDonor().getId().equals(donor.getId())) {
            throw new BadRequestException("Booking does not belong to donor");
        }
        if (BookingStatus.CANCELLED.equals(booking.getStatus())) {
            throw new ConflictException("Booking is cancelled");
        }
        return visitRepository.findByBookingId(booking.getId())
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
            throw new BadRequestException(fieldName + " is required");
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

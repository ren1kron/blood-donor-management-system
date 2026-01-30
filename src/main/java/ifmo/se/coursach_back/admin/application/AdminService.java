package ifmo.se.coursach_back.admin.application;

import ifmo.se.coursach_back.admin.api.dto.AdminRegisterDonorRequest;
import ifmo.se.coursach_back.admin.api.dto.AdminRegisterDonorResponse;
import ifmo.se.coursach_back.admin.api.dto.EligibleDonorResponse;
import ifmo.se.coursach_back.admin.api.dto.EligibleDonorRow;
import ifmo.se.coursach_back.admin.api.dto.ExpiredDocumentResponse;
import ifmo.se.coursach_back.admin.api.dto.ExpiredDocumentRow;
import ifmo.se.coursach_back.admin.api.dto.MarkNotifiedRequest;
import ifmo.se.coursach_back.admin.api.dto.NotificationMarkResponse;
import ifmo.se.coursach_back.admin.api.dto.ReportsSummaryResponse;
import ifmo.se.coursach_back.admin.api.dto.SendReminderRequest;
import ifmo.se.coursach_back.admin.api.dto.SendReminderResponse;
import ifmo.se.coursach_back.exception.BadRequestException;
import ifmo.se.coursach_back.exception.ConflictException;
import ifmo.se.coursach_back.exception.NotFoundException;
import ifmo.se.coursach_back.shared.domain.Account;
import ifmo.se.coursach_back.notification.domain.DeliveryStatus;
import ifmo.se.coursach_back.donor.domain.DocumentStatus;
import ifmo.se.coursach_back.donor.domain.DonorDocument;
import ifmo.se.coursach_back.donor.domain.DonorProfile;
import ifmo.se.coursach_back.donor.domain.DonorStatus;
import ifmo.se.coursach_back.lab.domain.LabExaminationStatus;
import ifmo.se.coursach_back.medical.domain.MedicalCheckDecision;
import ifmo.se.coursach_back.notification.domain.Notification;
import ifmo.se.coursach_back.notification.domain.NotificationDelivery;
import ifmo.se.coursach_back.shared.domain.Role;
import ifmo.se.coursach_back.admin.domain.StaffProfile;
import ifmo.se.coursach_back.shared.application.ports.AccountRepositoryPort;
import ifmo.se.coursach_back.medical.application.ports.DonationRepositoryPort;
import ifmo.se.coursach_back.donor.application.ports.DonorDocumentRepositoryPort;
import ifmo.se.coursach_back.donor.application.ports.DonorProfileRepositoryPort;
import ifmo.se.coursach_back.lab.application.ports.LabExaminationRequestRepositoryPort;
import ifmo.se.coursach_back.lab.application.ports.LabTestResultRepositoryPort;
import ifmo.se.coursach_back.medical.application.ports.MedicalCheckRepositoryPort;
import ifmo.se.coursach_back.notification.application.ports.NotificationDeliveryRepositoryPort;
import ifmo.se.coursach_back.notification.application.ports.NotificationRepositoryPort;
import ifmo.se.coursach_back.shared.application.ports.RoleRepositoryPort;
import ifmo.se.coursach_back.medical.application.ports.SampleRepositoryPort;
import ifmo.se.coursach_back.admin.application.ports.StaffProfileRepositoryPort;
import ifmo.se.coursach_back.donor.domain.BloodGroup;
import ifmo.se.coursach_back.donor.domain.RhFactor;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminService {
    private static final String DEFAULT_CHANNEL = "PHONE";
    private static final String TOPIC_REVISIT = "REVISIT";
    private static final String TOPIC_EXPIRED_DOCS = "EXPIRED_DOCS";

    private final AccountRepositoryPort accountRepository;
    private final RoleRepositoryPort roleRepository;
    private final DonorProfileRepositoryPort donorProfileRepository;
    private final StaffProfileRepositoryPort staffProfileRepository;
    private final DonationRepositoryPort donationRepository;
    private final DonorDocumentRepositoryPort donorDocumentRepository;
    private final NotificationRepositoryPort notificationRepository;
    private final NotificationDeliveryRepositoryPort notificationDeliveryRepository;
    private final SampleRepositoryPort sampleRepository;
    private final LabTestResultRepositoryPort labTestResultRepository;
    private final MedicalCheckRepositoryPort medicalCheckRepository;
    private final LabExaminationRequestRepositoryPort labExaminationRequestRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public AdminRegisterDonorResponse registerDonorByPhone(AdminRegisterDonorRequest request) {
        String phone = normalize(request.phone());
        String email = normalize(request.email());
        if (phone == null) {
            throw new BadRequestException("Phone is required");
        }
        if (accountRepository.existsByPhone(phone)) {
            throw new ConflictException("Phone is already in use");
        }
        if (email != null && accountRepository.existsByEmailIgnoreCase(email)) {
            throw new ConflictException("Email is already in use");
        }

        Role donorRole = roleRepository.findByCode("DONOR")
                .orElseThrow(() -> new BadRequestException("Role DONOR is not configured"));

        Account account = new Account();
        account.setPhone(phone);
        account.setEmail(email);
        account.setPasswordHash(passwordEncoder.encode(request.password()));
        account.setRoles(new java.util.HashSet<>());
        account.getRoles().add(donorRole);
        Account savedAccount = accountRepository.save(account);

        DonorProfile profile = new DonorProfile();
        profile.setAccount(savedAccount);
        profile.setFullName(request.fullName());
        profile.setBirthDate(request.birthDate());
        profile.setBloodGroup(BloodGroup.fromStringOrNull(request.bloodGroup()));
        profile.setRhFactor(RhFactor.fromStringOrNull(request.rhFactor()));
        DonorProfile savedProfile = donorProfileRepository.save(profile);

        return new AdminRegisterDonorResponse(savedAccount.getId(), savedProfile.getId(), request.password());
    }

    public List<EligibleDonorResponse> listEligibleDonors(int minDaysSinceDonation) {
        if (minDaysSinceDonation < 1) {
            throw new BadRequestException("minDaysSinceDonation must be positive");
        }
        OffsetDateTime now = OffsetDateTime.now();
        OffsetDateTime threshold = now.minusDays(minDaysSinceDonation);
        List<EligibleDonorRow> rows = donationRepository.findEligibleDonors(threshold);
        return rows.stream()
                .map(row -> new EligibleDonorResponse(
                        row.donorId(),
                        row.fullName(),
                        row.phone(),
                        row.email(),
                        row.lastDonationAt(),
                        ChronoUnit.DAYS.between(row.lastDonationAt(), now)
                ))
                .toList();
    }

    public List<ExpiredDocumentResponse> listExpiredDocuments(LocalDate asOf) {
        LocalDate date = asOf != null ? asOf : LocalDate.now();
        List<ExpiredDocumentRow> rows = donorDocumentRepository.findExpiredDocuments(date);
        return rows.stream()
                .map(row -> new ExpiredDocumentResponse(
                        row.documentId(),
                        row.donorId(),
                        row.fullName(),
                        row.phone(),
                        row.email(),
                        row.docType() != null ? row.docType().getValue() : null,
                        row.expiresAt()
                ))
                .toList();
    }

    @Transactional
    public NotificationMarkResponse markDonorRevisitNotified(UUID accountId, UUID donorId, MarkNotifiedRequest request) {
        DonorProfile donor = donorProfileRepository.findById(donorId)
                .orElseThrow(() -> new NotFoundException("Donor not found"));
        String body = normalize(request != null ? request.body() : null);
        if (body == null) {
            body = "You are eligible for repeat donation. Please contact the center to schedule your visit.";
        }
        return createNotification(accountId, donor, TOPIC_REVISIT, request, body);
    }

    @Transactional
    public NotificationMarkResponse markExpiredDocumentNotified(UUID accountId, UUID documentId,
                                                                MarkNotifiedRequest request) {
        DonorDocument document = donorDocumentRepository.findById(documentId)
                .orElseThrow(() -> new NotFoundException("Document not found"));

        if (document.getExpiresAt() != null && document.getExpiresAt().isBefore(LocalDate.now())) {
            if (document.getStatus() != DocumentStatus.EXPIRED) {
                document.setStatus(DocumentStatus.EXPIRED);
                donorDocumentRepository.save(document);
            }
        }

        DonorProfile donor = document.getDonor();
        String body = normalize(request != null ? request.body() : null);
        if (body == null) {
            body = "Your document %s is expired. Please update your information.".formatted(
                    document.getDocType() != null ? document.getDocType().getValue() : "UNKNOWN"
            );
        }
        return createNotification(accountId, donor, TOPIC_EXPIRED_DOCS, request, body);
    }

    private NotificationMarkResponse createNotification(UUID accountId, DonorProfile donor, String topic,
                                                        MarkNotifiedRequest request, String body) {
        String channel = normalize(request != null ? request.channel() : null);
        if (channel == null) {
            channel = DEFAULT_CHANNEL;
        }

        Notification notification = new Notification();
        notification.setChannel(channel);
        notification.setTopic(topic);
        notification.setBody(body);
        Notification savedNotification = notificationRepository.save(notification);

        StaffProfile staff = staffProfileRepository.findByAccountId(accountId).orElse(null);

        NotificationDelivery delivery = new NotificationDelivery();
        delivery.setNotification(savedNotification);
        delivery.setDonor(donor);
        delivery.setStaff(staff);
        delivery.setStatus(DeliveryStatus.SENT);
        OffsetDateTime sentAt = OffsetDateTime.now();
        delivery.setSentAt(sentAt);
        NotificationDelivery savedDelivery = notificationDeliveryRepository.save(delivery);

        return new NotificationMarkResponse(savedNotification.getId(), savedDelivery.getId(), sentAt);
    }

    private String normalize(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    public ReportsSummaryResponse getReportsSummary(OffsetDateTime from, OffsetDateTime to) {
        OffsetDateTime effectiveFrom = from != null ? from : OffsetDateTime.now().minusMonths(1);
        OffsetDateTime effectiveTo = to != null ? to : OffsetDateTime.now();
        
        long donorsTotalCount = donorProfileRepository.count();
        long donorsActiveCount = donorProfileRepository.countByDonorStatus(DonorStatus.ACTIVE);
        long donationsCount = donationRepository.countByPerformedAtBetween(effectiveFrom, effectiveTo);
        OffsetDateTime weekFrom = OffsetDateTime.now().minusDays(7);
        OffsetDateTime monthFrom = OffsetDateTime.now().minusMonths(1);
        long donationsLastWeek = donationRepository.countByPerformedAtBetween(weekFrom, OffsetDateTime.now());
        long donationsLastMonth = donationRepository.countByPerformedAtBetween(monthFrom, OffsetDateTime.now());
        long samplesCount = sampleRepository.countByCollectedAtBetween(effectiveFrom, effectiveTo);
        long publishedResultsCount = labTestResultRepository.countPublishedByTestedAtBetween(effectiveFrom, effectiveTo);
        long pendingReviewCount = medicalCheckRepository.countByStatus(MedicalCheckDecision.PENDING_REVIEW);
        long labQueueCount = labExaminationRequestRepository
                .countByStatusIn(List.of(LabExaminationStatus.REQUESTED, LabExaminationStatus.IN_PROGRESS));
        
        // Eligible candidates: donors who haven't donated in 60+ days
        OffsetDateTime threshold = OffsetDateTime.now().minusDays(60);
        List<EligibleDonorRow> eligible = donationRepository.findEligibleDonors(threshold);
        long eligibleCandidatesCount = eligible.size();
        
        // Blood units by group and Rh
        Map<String, Long> bloodUnitsByGroupRh = new HashMap<>();
        List<Object[]> volumeData = donationRepository.sumVolumeByBloodTypeAndRh(effectiveFrom, effectiveTo);
        for (Object[] row : volumeData) {
            String bloodType = row[0] != null ? row[0].toString() : "UNKNOWN";
            String rhFactor = row[1] != null ? row[1].toString() : "";
            Long volume = row[2] != null ? ((Number) row[2]).longValue() : 0L;
            String key = bloodType + (rhFactor.isEmpty() ? "" : rhFactor);
            bloodUnitsByGroupRh.put(key, volume);
        }
        
        return new ReportsSummaryResponse(
            donorsTotalCount,
            donorsActiveCount,
            donationsCount,
            donationsLastWeek,
            donationsLastMonth,
            samplesCount,
            publishedResultsCount,
            eligibleCandidatesCount,
            pendingReviewCount,
            labQueueCount,
            bloodUnitsByGroupRh
        );
    }

    @Transactional
    public SendReminderResponse sendReminder(UUID accountId, SendReminderRequest request) {
        DonorProfile donor = donorProfileRepository.findById(request.donorId())
                .orElseThrow(() -> new NotFoundException("Donor not found"));
        
        String channel = normalize(request.channel());
        if (channel == null) {
            channel = DEFAULT_CHANNEL;
        }
        
        String topic = normalize(request.topic());
        if (topic == null) {
            topic = "REMINDER";
        }
        
        String body = normalize(request.body());
        if (body == null) {
            throw new BadRequestException("Body is required");
        }
        
        Notification notification = new Notification();
        notification.setChannel(channel);
        notification.setTopic(topic);
        notification.setBody(body);
        Notification savedNotification = notificationRepository.save(notification);
        
        StaffProfile staff = staffProfileRepository.findByAccountId(accountId).orElse(null);
        
        NotificationDelivery delivery = new NotificationDelivery();
        delivery.setNotification(savedNotification);
        delivery.setDonor(donor);
        delivery.setStaff(staff);
        delivery.setStatus(DeliveryStatus.SENT);
        OffsetDateTime sentAt = OffsetDateTime.now();
        delivery.setSentAt(sentAt);
        NotificationDelivery savedDelivery = notificationDeliveryRepository.save(delivery);
        
        return new SendReminderResponse(
            savedNotification.getId(),
            savedDelivery.getId(),
            "SENT",
            sentAt
        );
    }
}

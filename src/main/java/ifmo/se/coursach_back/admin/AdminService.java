package ifmo.se.coursach_back.admin;

import ifmo.se.coursach_back.admin.dto.AdminRegisterDonorRequest;
import ifmo.se.coursach_back.admin.dto.AdminRegisterDonorResponse;
import ifmo.se.coursach_back.admin.dto.EligibleDonorResponse;
import ifmo.se.coursach_back.admin.dto.EligibleDonorProjection;
import ifmo.se.coursach_back.admin.dto.ExpiredDocumentResponse;
import ifmo.se.coursach_back.admin.dto.ExpiredDocumentProjection;
import ifmo.se.coursach_back.admin.dto.MarkNotifiedRequest;
import ifmo.se.coursach_back.admin.dto.NotificationMarkResponse;
import ifmo.se.coursach_back.model.Account;
import ifmo.se.coursach_back.model.DonorDocument;
import ifmo.se.coursach_back.model.DonorProfile;
import ifmo.se.coursach_back.model.Notification;
import ifmo.se.coursach_back.model.NotificationDelivery;
import ifmo.se.coursach_back.model.Role;
import ifmo.se.coursach_back.model.StaffProfile;
import ifmo.se.coursach_back.repository.AccountRepository;
import ifmo.se.coursach_back.repository.DonationRepository;
import ifmo.se.coursach_back.repository.DonorDocumentRepository;
import ifmo.se.coursach_back.repository.DonorProfileRepository;
import ifmo.se.coursach_back.repository.NotificationDeliveryRepository;
import ifmo.se.coursach_back.repository.NotificationRepository;
import ifmo.se.coursach_back.repository.RoleRepository;
import ifmo.se.coursach_back.repository.StaffProfileRepository;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AdminService {
    private static final String DEFAULT_CHANNEL = "PHONE";
    private static final String TOPIC_REVISIT = "REVISIT";
    private static final String TOPIC_EXPIRED_DOCS = "EXPIRED_DOCS";

    private final AccountRepository accountRepository;
    private final RoleRepository roleRepository;
    private final DonorProfileRepository donorProfileRepository;
    private final StaffProfileRepository staffProfileRepository;
    private final DonationRepository donationRepository;
    private final DonorDocumentRepository donorDocumentRepository;
    private final NotificationRepository notificationRepository;
    private final NotificationDeliveryRepository notificationDeliveryRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public AdminRegisterDonorResponse registerDonorByPhone(AdminRegisterDonorRequest request) {
        String phone = normalize(request.phone());
        String email = normalize(request.email());
        if (phone == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Phone is required");
        }
        if (accountRepository.existsByPhone(phone)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Phone is already in use");
        }
        if (email != null && accountRepository.existsByEmailIgnoreCase(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email is already in use");
        }

        Role donorRole = roleRepository.findByCode("DONOR")
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Role DONOR is not configured"));

        Account account = new Account();
        account.setPhone(phone);
        account.setEmail(email);
        account.setPasswordHash(passwordEncoder.encode(request.password()));
        account.getRoles().add(donorRole);
        Account savedAccount = accountRepository.save(account);

        DonorProfile profile = new DonorProfile();
        profile.setAccount(savedAccount);
        profile.setFullName(request.fullName());
        profile.setBirthDate(request.birthDate());
        profile.setBloodGroup(normalize(request.bloodGroup()));
        profile.setRhFactor(normalize(request.rhFactor()));
        DonorProfile savedProfile = donorProfileRepository.save(profile);

        return new AdminRegisterDonorResponse(savedAccount.getId(), savedProfile.getId());
    }

    public List<EligibleDonorResponse> listEligibleDonors(int minDaysSinceDonation) {
        if (minDaysSinceDonation < 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "minDaysSinceDonation must be positive");
        }
        OffsetDateTime now = OffsetDateTime.now();
        OffsetDateTime threshold = now.minusDays(minDaysSinceDonation);
        List<EligibleDonorProjection> rows = donationRepository.findEligibleDonors(threshold);
        return rows.stream()
                .map(row -> new EligibleDonorResponse(
                        row.getDonorId(),
                        row.getFullName(),
                        row.getPhone(),
                        row.getEmail(),
                        row.getLastDonationAt(),
                        ChronoUnit.DAYS.between(row.getLastDonationAt(), now)
                ))
                .toList();
    }

    public List<ExpiredDocumentResponse> listExpiredDocuments(LocalDate asOf) {
        LocalDate date = asOf != null ? asOf : LocalDate.now();
        List<ExpiredDocumentProjection> rows = donorDocumentRepository.findExpiredDocuments(date);
        return rows.stream()
                .map(row -> new ExpiredDocumentResponse(
                        row.getDocumentId(),
                        row.getDonorId(),
                        row.getFullName(),
                        row.getPhone(),
                        row.getEmail(),
                        row.getDocType(),
                        row.getExpiresAt()
                ))
                .toList();
    }

    @Transactional
    public NotificationMarkResponse markDonorRevisitNotified(UUID accountId, UUID donorId, MarkNotifiedRequest request) {
        DonorProfile donor = donorProfileRepository.findById(donorId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Donor not found"));
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
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Document not found"));

        if (document.getExpiresAt() != null && document.getExpiresAt().isBefore(LocalDate.now())) {
            String currentStatus = normalize(document.getStatus());
            if (currentStatus == null || !"EXPIRED".equalsIgnoreCase(currentStatus)) {
                document.setStatus("EXPIRED");
                donorDocumentRepository.save(document);
            }
        }

        DonorProfile donor = document.getDonor();
        String body = normalize(request != null ? request.body() : null);
        if (body == null) {
            body = "Your document %s is expired. Please update your information.".formatted(document.getDocType());
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
        delivery.setStatus("SENT");
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
}

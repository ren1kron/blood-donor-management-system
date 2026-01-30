package ifmo.se.coursach_back.report.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import ifmo.se.coursach_back.shared.application.ports.DomainEventPublisher;
import ifmo.se.coursach_back.shared.domain.event.AuditDomainEvent;
import ifmo.se.coursach_back.exception.BadRequestException;
import ifmo.se.coursach_back.exception.NotFoundException;
import ifmo.se.coursach_back.shared.domain.Account;
import ifmo.se.coursach_back.medical.domain.Deferral;
import ifmo.se.coursach_back.notification.domain.DeliveryStatus;
import ifmo.se.coursach_back.medical.domain.Donation;
import ifmo.se.coursach_back.donor.domain.DonorProfile;
import ifmo.se.coursach_back.lab.domain.LabTestResult;
import ifmo.se.coursach_back.medical.domain.MedicalCheck;
import ifmo.se.coursach_back.notification.domain.Notification;
import ifmo.se.coursach_back.notification.domain.NotificationDelivery;
import ifmo.se.coursach_back.report.domain.ReportRequest;
import ifmo.se.coursach_back.report.domain.ReportRequestStatus;
import ifmo.se.coursach_back.report.domain.ReportType;
import ifmo.se.coursach_back.shared.domain.Role;
import ifmo.se.coursach_back.medical.domain.Sample;
import ifmo.se.coursach_back.admin.domain.StaffProfile;
import ifmo.se.coursach_back.shared.application.ports.AccountRepositoryPort;
import ifmo.se.coursach_back.medical.application.ports.DeferralRepositoryPort;
import ifmo.se.coursach_back.medical.application.ports.DonationRepositoryPort;
import ifmo.se.coursach_back.donor.application.ports.DonorProfileRepositoryPort;
import ifmo.se.coursach_back.lab.application.ports.LabTestResultRepositoryPort;
import ifmo.se.coursach_back.medical.application.ports.MedicalCheckRepositoryPort;
import ifmo.se.coursach_back.notification.application.ports.NotificationDeliveryRepositoryPort;
import ifmo.se.coursach_back.notification.application.ports.NotificationRepositoryPort;
import ifmo.se.coursach_back.report.application.ports.ReportRequestRepositoryPort;
import ifmo.se.coursach_back.medical.application.ports.SampleRepositoryPort;
import ifmo.se.coursach_back.admin.application.ports.StaffProfileRepositoryPort;
import ifmo.se.coursach_back.report.api.dto.ReportRequestActionRequest;
import ifmo.se.coursach_back.report.api.dto.ReportRequestCreateRequest;
import ifmo.se.coursach_back.report.api.dto.ReportRequestDetailsResponse;
import ifmo.se.coursach_back.report.api.dto.ReportRequestSummaryResponse;
import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class ReportRequestService {
    private static final Set<ReportType> SUPPORTED_TYPES = Set.of(
            ReportType.DONOR_SUMMARY,
            ReportType.LAB_OVERVIEW,
            ReportType.ELIGIBILITY
    );

    private final ReportRequestRepositoryPort reportRequestRepository;
    private final DonorProfileRepositoryPort donorProfileRepository;
    private final StaffProfileRepositoryPort staffProfileRepository;
    private final AccountRepositoryPort accountRepository;
    private final DonationRepositoryPort donationRepository;
    private final MedicalCheckRepositoryPort medicalCheckRepository;
    private final DeferralRepositoryPort deferralRepository;
    private final SampleRepositoryPort sampleRepository;
    private final LabTestResultRepositoryPort labTestResultRepository;
    private final NotificationRepositoryPort notificationRepository;
    private final NotificationDeliveryRepositoryPort notificationDeliveryRepository;
    private final ObjectMapper objectMapper;
    private final DomainEventPublisher eventPublisher;

    @Transactional
    public ReportRequestSummaryResponse createRequest(UUID accountId, ReportRequestCreateRequest request) {
        StaffProfile requester = requireStaff(accountId);
        DonorProfile donor = donorProfileRepository.findById(request.donorId())
                .orElseThrow(() -> new NotFoundException("Donor not found"));

        ReportType reportType = request.reportType();
        if (reportType == null) {
            throw new BadRequestException("reportType is required");
        }
        if (!SUPPORTED_TYPES.contains(reportType)) {
            throw new BadRequestException("Unsupported report type");
        }

        ReportRequest reportRequest = new ReportRequest();
        reportRequest.setDonor(donor);
        reportRequest.setRequestedBy(requester);
        reportRequest.setRequestedByRole(resolveStaffRole(requester.getAccount()));
        reportRequest.setReportType(reportType);
        reportRequest.setStatus(ReportRequestStatus.REQUESTED);
        reportRequest.setMessage(normalize(request.comment()));
        ReportRequest saved = reportRequestRepository.save(reportRequest);

        eventPublisher.publish(AuditDomainEvent.of(accountId, "REPORT_REQUEST_CREATED", "ReportRequest", saved.getId(),
                Map.of("reportType", reportType.name(), "donorId", donor.getId())));
        return toSummary(saved);
    }

    public List<ReportRequestSummaryResponse> listMyRequests(UUID accountId) {
        StaffProfile requester = requireStaff(accountId);
        return reportRequestRepository.findByRequestedBy_IdOrderByCreatedAtDesc(requester.getId()).stream()
                .map(this::toSummary)
                .toList();
    }

    public ReportRequestDetailsResponse getReport(UUID accountId, UUID requestId) {
        StaffProfile staff = requireStaff(accountId);
        ReportRequest request = reportRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Report request not found"));

        if (!isAuthorized(staff, accountId, request)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }

        Object payload = parseJson(request.getPayloadJson());
        return toDetails(request, payload);
    }

    public List<ReportRequestSummaryResponse> listRequestsForAdmin(String status) {
        List<ReportRequest> requests;
        if (status == null || status.isBlank()) {
            requests = reportRequestRepository.findAll();
        } else {
            try {
                ReportRequestStatus statusEnum = ReportRequestStatus.valueOf(status.trim().toUpperCase(Locale.ROOT));
                requests = reportRequestRepository.findByStatusOrderByCreatedAtAsc(statusEnum);
            } catch (IllegalArgumentException e) {
                throw new BadRequestException("Invalid status: " + status);
            }
        }
        return requests.stream().map(this::toSummary).toList();
    }

    @Transactional
    public ReportRequestSummaryResponse takeRequest(UUID accountId, UUID requestId) {
        StaffProfile admin = requireStaff(accountId);
        ReportRequest request = reportRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Report request not found"));
        if (request.getAssignedAdmin() != null
                && !request.getAssignedAdmin().getId().equals(admin.getId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Request is already assigned");
        }
        request.setAssignedAdmin(admin);
        request.setStatus(ReportRequestStatus.IN_PROGRESS);
        ReportRequest saved = reportRequestRepository.save(request);
        eventPublisher.publish(AuditDomainEvent.of(accountId, "REPORT_REQUEST_TAKEN", "ReportRequest", saved.getId()));
        return toSummary(saved);
    }

    @Transactional
    public ReportRequestDetailsResponse generateReport(UUID accountId, UUID requestId) {
        StaffProfile admin = requireStaff(accountId);
        ReportRequest request = reportRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Report request not found"));
        ensureAssigned(admin, request);

        Map<String, Object> payload = buildPayload(request);
        request.setPayloadJson(toJson(payload));
        request.setGeneratedAt(OffsetDateTime.now());
        request.setStatus(ReportRequestStatus.READY);
        ReportRequest saved = reportRequestRepository.save(request);
        eventPublisher.publish(AuditDomainEvent.of(accountId, "REPORT_GENERATED", "ReportRequest", saved.getId(),
                Map.of("reportType", saved.getReportType())));

        return toDetails(saved, payload);
    }

    @Transactional
    public ReportRequestSummaryResponse sendReport(UUID accountId, UUID requestId) {
        StaffProfile admin = requireStaff(accountId);
        ReportRequest request = reportRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Report request not found"));
        ensureAssigned(admin, request);
        if (request.getStatus() != ReportRequestStatus.READY) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Report is not ready to send");
        }

        Notification notification = new Notification();
        notification.setChannel("IN_APP");
        notification.setTopic("REPORT");
        notification.setBody(buildNotificationBody(request));
        Notification savedNotification = notificationRepository.save(notification);

        NotificationDelivery delivery = new NotificationDelivery();
        delivery.setNotification(savedNotification);
        delivery.setStaff(request.getRequestedBy());
        delivery.setStatus(DeliveryStatus.SENT);
        delivery.setSentAt(OffsetDateTime.now());
        notificationDeliveryRepository.save(delivery);

        request.setStatus(ReportRequestStatus.SENT);
        ReportRequest saved = reportRequestRepository.save(request);
        eventPublisher.publish(AuditDomainEvent.of(accountId, "REPORT_SENT", "ReportRequest", saved.getId()));
        return toSummary(saved);
    }

    @Transactional
    public ReportRequestSummaryResponse rejectReport(UUID accountId, UUID requestId, ReportRequestActionRequest action) {
        StaffProfile admin = requireStaff(accountId);
        ReportRequest request = reportRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Report request not found"));
        if (request.getAssignedAdmin() != null
                && !request.getAssignedAdmin().getId().equals(admin.getId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Request is assigned to another admin");
        }
        request.setAssignedAdmin(admin);
        request.setStatus(ReportRequestStatus.REJECTED);
        if (action != null && action.message() != null) {
            request.setMessage(action.message().trim());
        }
        ReportRequest saved = reportRequestRepository.save(request);
        eventPublisher.publish(AuditDomainEvent.of(accountId, "REPORT_REJECTED", "ReportRequest", saved.getId()));
        return toSummary(saved);
    }

    private StaffProfile requireStaff(UUID accountId) {
        return staffProfileRepository.findByAccountId(accountId)
                .orElseThrow(() -> new NotFoundException("Staff profile not found"));
    }

    private boolean isAuthorized(StaffProfile staff, UUID accountId, ReportRequest request) {
        if (request.getRequestedBy().getId().equals(staff.getId())) {
            return true;
        }
        if (request.getAssignedAdmin() != null && request.getAssignedAdmin().getId().equals(staff.getId())) {
            return true;
        }
        return hasRole(accountId, "ADMIN");
    }

    private void ensureAssigned(StaffProfile admin, ReportRequest request) {
        if (request.getAssignedAdmin() == null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Request must be taken first");
        }
        if (!request.getAssignedAdmin().getId().equals(admin.getId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Request is assigned to another admin");
        }
    }

    private ReportRequestSummaryResponse toSummary(ReportRequest request) {
        return new ReportRequestSummaryResponse(
                request.getId(),
                request.getDonor().getId(),
                request.getDonor().getFullName(),
                request.getReportType(),
                request.getStatus(),
                request.getRequestedBy() != null ? request.getRequestedBy().getFullName() : null,
                request.getRequestedByRole(),
                request.getAssignedAdmin() != null ? request.getAssignedAdmin().getFullName() : null,
                request.getCreatedAt(),
                request.getUpdatedAt(),
                request.getGeneratedAt(),
                request.getMessage()
        );
    }

    private ReportRequestDetailsResponse toDetails(ReportRequest request, Object payload) {
        return new ReportRequestDetailsResponse(
                request.getId(),
                request.getDonor().getId(),
                request.getDonor().getFullName(),
                request.getReportType(),
                request.getStatus(),
                request.getRequestedBy() != null ? request.getRequestedBy().getFullName() : null,
                request.getRequestedByRole(),
                request.getAssignedAdmin() != null ? request.getAssignedAdmin().getFullName() : null,
                request.getCreatedAt(),
                request.getUpdatedAt(),
                request.getGeneratedAt(),
                request.getMessage(),
                payload
        );
    }

    private Map<String, Object> buildPayload(ReportRequest request) {
        ReportType type = request.getReportType();
        DonorProfile donor = request.getDonor();
        if (type == ReportType.DONOR_SUMMARY) {
            return buildDonorSummary(donor);
        }
        if (type == ReportType.LAB_OVERVIEW) {
            return buildLabOverview(donor);
        }
        if (type == ReportType.ELIGIBILITY) {
            return buildEligibility(donor);
        }
        throw new BadRequestException("Unsupported report type");
    }

    private Map<String, Object> buildDonorSummary(DonorProfile donor) {
        Map<String, Object> payload = new LinkedHashMap<>();
        Map<String, Object> donorInfo = new LinkedHashMap<>();
        donorInfo.put("id", donor.getId());
        donorInfo.put("fullName", donor.getFullName());
        donorInfo.put("birthDate", donor.getBirthDate());
        donorInfo.put("bloodGroup", donor.getBloodGroup());
        donorInfo.put("rhFactor", donor.getRhFactor());
        donorInfo.put("donorStatus", donor.getDonorStatus());
        donorInfo.put("email", donor.getAccount().getEmail());
        donorInfo.put("phone", donor.getAccount().getPhone());
        payload.put("donor", donorInfo);

        MedicalCheck latestCheck = medicalCheckRepository
                .findTopByVisit_Booking_Donor_IdOrderByDecisionAtDesc(donor.getId())
                .orElse(null);
        if (latestCheck == null) {
            payload.put("latestMedicalCheck", null);
        } else {
            Map<String, Object> checkInfo = new LinkedHashMap<>();
            checkInfo.put("id", latestCheck.getId());
            checkInfo.put("decision", latestCheck.getDecision());
            checkInfo.put("decisionAt", latestCheck.getDecisionAt());
            checkInfo.put("weightKg", latestCheck.getWeightKg());
            checkInfo.put("hemoglobinGl", latestCheck.getHemoglobinGl());
            checkInfo.put("systolicMmhg", latestCheck.getSystolicMmhg());
            checkInfo.put("diastolicMmhg", latestCheck.getDiastolicMmhg());
            checkInfo.put("pulseRate", latestCheck.getPulseRate());
            checkInfo.put("bodyTemperatureC", latestCheck.getBodyTemperatureC());
            payload.put("latestMedicalCheck", checkInfo);
        }

        Deferral activeDeferral = deferralRepository
                .findActiveDeferral(donor.getId(), OffsetDateTime.now())
                .orElse(null);
        if (activeDeferral == null) {
            payload.put("activeDeferral", null);
        } else {
            Map<String, Object> deferralInfo = new LinkedHashMap<>();
            deferralInfo.put("id", activeDeferral.getId());
            deferralInfo.put("deferralType", activeDeferral.getDeferralType());
            deferralInfo.put("reason", activeDeferral.getReason());
            deferralInfo.put("startsAt", activeDeferral.getStartsAt());
            deferralInfo.put("endsAt", activeDeferral.getEndsAt());
            payload.put("activeDeferral", deferralInfo);
        }

        List<Donation> donations = donationRepository.findByDonorAccountId(donor.getAccount().getId());
        List<Map<String, Object>> donationItems = donations.stream()
                .limit(10)
                .map(donation -> {
                    Map<String, Object> item = new LinkedHashMap<>();
                    item.put("id", donation.getId());
                    item.put("performedAt", donation.getPerformedAt());
                    item.put("donationType", donation.getDonationType() != null ? donation.getDonationType().getValue() : null);
                    item.put("volumeMl", donation.getVolumeMl());
                    item.put("published", donation.isPublished());
                    return item;
                })
                .toList();
        payload.put("donations", donationItems);

        return payload;
    }

    private Map<String, Object> buildLabOverview(DonorProfile donor) {
        Map<String, Object> payload = new LinkedHashMap<>();
        List<Sample> samples = sampleRepository.findByDonorId(donor.getId());
        List<Map<String, Object>> sampleItems = samples.stream()
                .map(sample -> {
                    Map<String, Object> item = new LinkedHashMap<>();
                    item.put("id", sample.getId());
                    item.put("sampleCode", sample.getSampleCode());
                    item.put("collectedAt", sample.getCollectedAt());
                    item.put("status", sample.getStatus());
                    item.put("donationId", sample.getDonation().getId());
                    return item;
                })
                .toList();
        payload.put("samples", sampleItems);

        List<LabTestResult> results = labTestResultRepository.findByDonorId(donor.getId());
        List<Map<String, Object>> resultItems = results.stream()
                .map(result -> {
                    Map<String, Object> item = new LinkedHashMap<>();
                    item.put("id", result.getId());
                    item.put("sampleId", result.getSample().getId());
                    item.put("testType", result.getTestType().getCode());
                    item.put("testName", result.getTestType().getName());
                    item.put("resultValue", result.getResultValue());
                    item.put("resultFlag", result.getResultFlag());
                    item.put("testedAt", result.getTestedAt());
                    item.put("published", result.isPublished());
                    return item;
                })
                .toList();
        payload.put("labResults", resultItems);

        return payload;
    }

    private Map<String, Object> buildEligibility(DonorProfile donor) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("donorStatus", donor.getDonorStatus());

        Deferral activeDeferral = deferralRepository.findActiveDeferral(donor.getId(), OffsetDateTime.now())
                .orElse(null);
        if (activeDeferral == null) {
            payload.put("activeDeferral", null);
        } else {
            Map<String, Object> deferralInfo = new LinkedHashMap<>();
            deferralInfo.put("deferralType", activeDeferral.getDeferralType());
            deferralInfo.put("reason", activeDeferral.getReason());
            deferralInfo.put("endsAt", activeDeferral.getEndsAt());
            payload.put("activeDeferral", deferralInfo);
        }

        Donation lastDonation = donationRepository
                .findTopByVisit_Booking_Donor_Account_IdOrderByPerformedAtDesc(donor.getAccount().getId())
                .orElse(null);
        OffsetDateTime lastDonationAt = lastDonation != null ? lastDonation.getPerformedAt() : null;
        payload.put("lastDonationAt", lastDonationAt);

        MedicalCheck latestCheck = medicalCheckRepository
                .findTopByVisit_Booking_Donor_IdOrderByDecisionAtDesc(donor.getId())
                .orElse(null);
        payload.put("latestMedicalDecision", latestCheck == null ? null : latestCheck.getDecision());
        payload.put("latestMedicalDecisionAt", latestCheck == null ? null : latestCheck.getDecisionAt());

        OffsetDateTime nextEligibleAt = lastDonationAt != null ? lastDonationAt.plusDays(56) : null;
        if (activeDeferral != null) {
            if (activeDeferral.getEndsAt() != null) {
                if (nextEligibleAt == null || activeDeferral.getEndsAt().isAfter(nextEligibleAt)) {
                    nextEligibleAt = activeDeferral.getEndsAt();
                }
            } else {
                nextEligibleAt = null;
            }
        }
        payload.put("nextEligibleAt", nextEligibleAt);

        return payload;
    }

    private String resolveStaffRole(Account account) {
        if (account == null || account.getRoles() == null) {
            return null;
        }
        List<String> codes = account.getRoles().stream()
                .map(Role::getCode)
                .sorted()
                .collect(Collectors.toList());
        for (String role : List.of("DOCTOR", "LAB", "NURSE", "ADMIN")) {
            if (codes.contains(role)) {
                return role;
            }
        }
        return codes.isEmpty() ? null : codes.get(0);
    }

    private boolean hasRole(UUID accountId, String roleCode) {
        Optional<Account> account = accountRepository.findById(accountId);
        return account.map(value -> value.getRoles().stream()
                .anyMatch(role -> roleCode.equalsIgnoreCase(role.getCode())))
                .orElse(false);
    }

    private String normalize(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }


    private String toJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new BadRequestException("Failed to serialize report payload");
        }
    }

    private Object parseJson(String json) {
        if (json == null || json.isBlank()) {
            return null;
        }
        try {
            return objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {
            });
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    private String buildNotificationBody(ReportRequest request) {
        return "Отчет готов: %s. Донор: %s. Запрос: %s".formatted(
                request.getReportType(),
                request.getDonor().getFullName(),
                request.getId()
        );
    }
}

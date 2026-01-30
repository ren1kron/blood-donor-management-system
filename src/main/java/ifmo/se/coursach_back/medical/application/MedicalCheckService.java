package ifmo.se.coursach_back.medical.application;

import ifmo.se.coursach_back.audit.application.AuditService;
import ifmo.se.coursach_back.shared.application.EntityResolverService;
import ifmo.se.coursach_back.shared.util.EnumUtils;
import ifmo.se.coursach_back.exception.BadRequestException;
import ifmo.se.coursach_back.exception.ConflictException;
import ifmo.se.coursach_back.exception.NotFoundException;
import ifmo.se.coursach_back.medical.api.dto.DeferralRequest;
import ifmo.se.coursach_back.medical.api.dto.ExaminationDecisionRequest;
import ifmo.se.coursach_back.medical.api.dto.MedicalCheckRequest;
import ifmo.se.coursach_back.medical.api.dto.ReviewExaminationRequest;
import ifmo.se.coursach_back.medical.domain.Deferral;
import ifmo.se.coursach_back.medical.domain.DeferralType;
import ifmo.se.coursach_back.donor.domain.DonorProfile;
import ifmo.se.coursach_back.lab.domain.LabExaminationRequest;
import ifmo.se.coursach_back.lab.domain.LabExaminationStatus;
import ifmo.se.coursach_back.medical.domain.MedicalCheck;
import ifmo.se.coursach_back.medical.domain.MedicalCheckDecision;
import ifmo.se.coursach_back.admin.domain.StaffProfile;
import ifmo.se.coursach_back.appointment.domain.Visit;
import ifmo.se.coursach_back.notification.application.NotificationService;
import ifmo.se.coursach_back.notification.domain.NotificationTopics;
import ifmo.se.coursach_back.medical.infra.jpa.DeferralRepository;
import ifmo.se.coursach_back.lab.infra.jpa.LabExaminationRequestRepository;
import ifmo.se.coursach_back.medical.infra.jpa.MedicalCheckRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Service for medical check operations.
 * Handles medical examinations, decisions, and deferrals.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MedicalCheckService {

    private final MedicalCheckRepository medicalCheckRepository;
    private final DeferralRepository deferralRepository;
    private final LabExaminationRequestRepository labExaminationRequestRepository;
    private final EntityResolverService entityResolver;
    private final NotificationService notificationService;
    private final AuditService auditService;

    /**
     * Lists all examinations pending doctor review.
     */
    public List<MedicalCheck> listPendingExaminations() {
        return medicalCheckRepository.findByStatusOrderBySubmittedAtAsc(MedicalCheckDecision.PENDING_REVIEW);
    }

    /**
     * Finds the latest medical check for a donor.
     */
    public MedicalCheck findLatestByDonor(UUID donorId) {
        return medicalCheckRepository.findTopByVisit_Booking_Donor_IdOrderByDecisionAtDesc(donorId)
                .orElse(null);
    }

    /**
     * Reviews an examination and makes a decision.
     */
    @Transactional
    public MedicalCheckResult reviewExamination(UUID accountId, ReviewExaminationRequest request) {
        StaffProfile doctor = entityResolver.requireStaff(accountId);

        MedicalCheck check = medicalCheckRepository.findById(request.examinationId())
                .orElseThrow(() -> NotFoundException.entity("Medical check", request.examinationId()));

        validateLabExaminationCompleted(check.getVisit().getId());
        validatePendingReview(check);

        MedicalCheckDecision decision = parseAndValidateDecision(request.decision(), request.deferral());

        applyDecision(check, doctor, decision);
        MedicalCheck saved = medicalCheckRepository.save(check);

        Deferral savedDeferral = null;
        if (request.deferral() != null) {
            savedDeferral = createDeferral(check.getVisit().getBooking().getDonor(), saved, request.deferral());
        }

        sendDecisionNotification(check.getVisit().getBooking().getDonor(), decision, request.deferral());

        auditService.log(accountId, "MEDICAL_CHECK_DECISION", "MedicalCheck", saved.getId(),
                Map.of("decision", decision.name()));

        return new MedicalCheckResult(saved, savedDeferral);
    }

    /**
     * Records a medical check with a decision.
     */
    @Transactional
    public MedicalCheckResult recordMedicalCheck(UUID accountId, MedicalCheckRequest request) {
        StaffProfile staff = entityResolver.requireStaff(accountId);
        Visit visit = entityResolver.resolveVisit(request.bookingId(), request.visitId());

        validateLabExaminationCompleted(visit.getId());

        MedicalCheckDecision decision = parseAndValidateDecision(request.decision(), request.deferral());

        MedicalCheck check = medicalCheckRepository.findByVisit_Id(visit.getId())
                .orElseGet(MedicalCheck::new);

        check.setVisit(visit);
        check.setPerformedBy(staff);
        check.setWeightKg(request.weightKg());
        check.setHemoglobinGl(request.hemoglobinGl());
        check.setSystolicMmhg(request.systolicMmhg());
        check.setDiastolicMmhg(request.diastolicMmhg());
        applyDecision(check, staff, decision);

        MedicalCheck saved = medicalCheckRepository.save(check);

        Deferral savedDeferral = null;
        if (request.deferral() != null) {
            savedDeferral = createDeferral(visit.getBooking().getDonor(), saved, request.deferral());
        }

        if (decision == MedicalCheckDecision.ADMITTED) {
            sendDecisionNotification(visit.getBooking().getDonor(), decision, null);
        }

        auditService.log(accountId, "MEDICAL_CHECK_DECISION", "MedicalCheck", saved.getId(),
                Map.of("decision", decision.name()));

        return new MedicalCheckResult(saved, savedDeferral);
    }

    /**
     * Makes a decision on an examination visit.
     */
    @Transactional
    public MedicalCheckResult decideExamination(UUID accountId, UUID visitId, ExaminationDecisionRequest request) {
        StaffProfile doctor = entityResolver.requireStaff(accountId);
        Visit visit = entityResolver.getVisit(visitId);

        validateLabExaminationCompleted(visitId);

        MedicalCheckDecision decision = parseAndValidateDecision(request.decision(), request.deferral());

        MedicalCheck check = medicalCheckRepository.findByVisit_Id(visit.getId())
                .orElseGet(MedicalCheck::new);

        check.setVisit(visit);
        applyDecision(check, doctor, decision);

        MedicalCheck saved = medicalCheckRepository.save(check);

        Deferral savedDeferral = null;
        if (request.deferral() != null) {
            savedDeferral = createDeferral(visit.getBooking().getDonor(), saved, request.deferral());
        }

        sendDecisionNotification(visit.getBooking().getDonor(), decision, request.deferral());

        auditService.log(accountId, "MEDICAL_CHECK_DECISION", "MedicalCheck", saved.getId(),
                Map.of("decision", decision.name()));

        return new MedicalCheckResult(saved, savedDeferral);
    }

    private void validateLabExaminationCompleted(UUID visitId) {
        LabExaminationRequest labRequest = labExaminationRequestRepository.findByVisit_Id(visitId)
                .orElse(null);

        if (labRequest == null || labRequest.getStatus() != LabExaminationStatus.COMPLETED) {
            throw new ConflictException("Lab examination is not completed");
        }
    }

    private void validatePendingReview(MedicalCheck check) {
        if (check.getStatus() != MedicalCheckDecision.PENDING_REVIEW) {
            throw ConflictException.alreadyInState("Examination", "reviewed");
        }
    }

    private MedicalCheckDecision parseAndValidateDecision(String decisionStr, DeferralRequest deferral) {
        MedicalCheckDecision decision = EnumUtils.parse(MedicalCheckDecision.class, decisionStr);

        if (decision != MedicalCheckDecision.ADMITTED && decision != MedicalCheckDecision.REFUSED) {
            throw new BadRequestException("Decision must be ADMITTED or REFUSED");
        }

        if (decision == MedicalCheckDecision.REFUSED && deferral == null) {
            throw new BadRequestException("Deferral is required when decision is REFUSED");
        }

        if (decision == MedicalCheckDecision.ADMITTED && deferral != null) {
            throw new BadRequestException("Deferral is not allowed when decision is ADMITTED");
        }

        return decision;
    }

    private void applyDecision(MedicalCheck check, StaffProfile performer, MedicalCheckDecision decision) {
        check.setPerformedBy(performer);
        check.setDecision(decision);
        check.setStatus(decision);
        check.setDecisionAt(OffsetDateTime.now());
    }

    private Deferral createDeferral(DonorProfile donor, MedicalCheck check, DeferralRequest request) {
        validateDeferral(request);

        Deferral deferral = new Deferral();
        deferral.setDonor(donor);
        deferral.setCreatedFromCheck(check);
        deferral.setDeferralType(EnumUtils.parse(DeferralType.class, request.deferralType()));
        deferral.setReason(request.reason());
        deferral.setEndsAt(request.endsAt());

        return deferralRepository.save(deferral);
    }

    private void validateDeferral(DeferralRequest request) {
        if (request.endsAt() != null && request.endsAt().isBefore(OffsetDateTime.now())) {
            throw new BadRequestException("Deferral end time cannot be in the past");
        }
    }

    private void sendDecisionNotification(DonorProfile donor, MedicalCheckDecision decision, DeferralRequest deferral) {
        String body;
        String topic;

        if (decision == MedicalCheckDecision.ADMITTED) {
            topic = NotificationTopics.MEDICAL_CHECK;
            body = "Вы успешно прошли медосмотр и можете записаться на донацию.";
        } else {
            topic = NotificationTopics.DEFERRAL;
            body = buildDeferralMessage(deferral);
        }

        notificationService.sendToDonor(
                NotificationService.NotificationRequest.builder()
                        .channel("IN_APP")
                        .topic(topic)
                        .body(body)
                        .donor(donor)
                        .build()
        );
    }

    private String buildDeferralMessage(DeferralRequest deferral) {
        StringBuilder body = new StringBuilder("К сожалению, по результатам медосмотра вы не допущены к донации.");

        if (deferral != null) {
            if (deferral.reason() != null) {
                body.append(" Причина: ").append(deferral.reason());
            }
            if (deferral.endsAt() != null) {
                body.append(" Повторная запись возможна после ").append(deferral.endsAt().toLocalDate());
            }
        }

        return body.toString();
    }

    /**
     * Result of a medical check operation.
     */
    public record MedicalCheckResult(MedicalCheck check, Deferral deferral) {
    }
}

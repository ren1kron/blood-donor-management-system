package ifmo.se.coursach_back.medical.application;

import ifmo.se.coursach_back.shared.application.EntityResolverService;
import ifmo.se.coursach_back.shared.util.EnumUtils;
import ifmo.se.coursach_back.exception.ConflictException;
import ifmo.se.coursach_back.exception.NotFoundException;
import ifmo.se.coursach_back.medical.api.dto.SampleRequest;
import ifmo.se.coursach_back.medical.domain.Donation;
import ifmo.se.coursach_back.medical.domain.Sample;
import ifmo.se.coursach_back.medical.domain.SampleStatus;
import ifmo.se.coursach_back.medical.infra.jpa.DonationRepository;
import ifmo.se.coursach_back.medical.infra.jpa.SampleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import static ifmo.se.coursach_back.shared.util.StringUtils.isBlank;
import static ifmo.se.coursach_back.shared.util.StringUtils.normalize;

/**
 * Service for sample registration and management.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SampleService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.BASIC_ISO_DATE;

    private final SampleRepository sampleRepository;
    private final DonationRepository donationRepository;

    /**
     * Registers a new sample for a donation.
     */
    @Transactional
    public Sample registerSample(SampleRequest request) {
        Donation donation = donationRepository.findById(request.donationId())
                .orElseThrow(() -> NotFoundException.entity("Donation", request.donationId()));

        String sampleCode = resolveSampleCode(request.sampleCode());
        validateUniqueCode(sampleCode);

        Sample sample = new Sample();
        sample.setDonation(donation);
        sample.setSampleCode(sampleCode);
        sample.setStatus(resolveStatus(request.status()));
        sample.setQuarantineReason(normalize(request.quarantineReason()));
        sample.setRejectionReason(normalize(request.rejectionReason()));

        Sample saved = sampleRepository.save(sample);
        log.info("Sample registered: sampleId={}, code={}, donationId={}",
                saved.getId(), sampleCode, donation.getId());

        return saved;
    }

    /**
     * Updates sample status.
     */
    @Transactional
    public Sample updateStatus(UUID sampleId, String status, String reason) {
        Sample sample = sampleRepository.findById(sampleId)
                .orElseThrow(() -> NotFoundException.entity("Sample", sampleId));

        SampleStatus newStatus = EnumUtils.parse(SampleStatus.class, status);
        sample.setStatus(newStatus);

        if (newStatus == SampleStatus.QUARANTINE) {
            sample.setQuarantineReason(normalize(reason));
        } else if (newStatus == SampleStatus.REJECTED) {
            sample.setRejectionReason(normalize(reason));
        }

        Sample saved = sampleRepository.save(sample);
        log.info("Sample status updated: sampleId={}, newStatus={}", sampleId, newStatus);

        return saved;
    }

    private String resolveSampleCode(String code) {
        if (isBlank(code)) {
            return generateSampleCode();
        }
        return code.trim();
    }

    private void validateUniqueCode(String code) {
        if (sampleRepository.existsBySampleCode(code)) {
            throw ConflictException.duplicate("Sample", "code", code);
        }
    }

    private SampleStatus resolveStatus(String status) {
        return EnumUtils.parseNullable(SampleStatus.class, status)
                .orElse(SampleStatus.NEW);
    }

    private String generateSampleCode() {
        String datePart = LocalDate.now().format(DATE_FORMATTER);
        String randomPart = UUID.randomUUID().toString().substring(0, 5).toUpperCase();
        return "SAM-" + datePart + "-" + randomPart;
    }
}

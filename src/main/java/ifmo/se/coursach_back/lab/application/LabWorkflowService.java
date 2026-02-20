package ifmo.se.coursach_back.lab.application;

import ifmo.se.coursach_back.lab.api.dto.LabTestResultRequest;
import ifmo.se.coursach_back.lab.domain.LabExaminationRequest;
import ifmo.se.coursach_back.lab.domain.LabExaminationStatus;
import ifmo.se.coursach_back.lab.domain.LabTestResult;
import ifmo.se.coursach_back.lab.domain.LabTestType;
import ifmo.se.coursach_back.donor.domain.BloodGroup;
import ifmo.se.coursach_back.donor.domain.DonorProfile;
import ifmo.se.coursach_back.donor.domain.RhFactor;
import ifmo.se.coursach_back.medical.domain.MedicalCheck;
import ifmo.se.coursach_back.medical.domain.MedicalCheckDecision;
import ifmo.se.coursach_back.medical.domain.Sample;
import ifmo.se.coursach_back.medical.domain.SampleStatus;
import ifmo.se.coursach_back.admin.domain.StaffProfile;
import java.util.Arrays;
import ifmo.se.coursach_back.donor.application.ports.DonorProfileRepositoryPort;
import ifmo.se.coursach_back.lab.application.ports.LabExaminationRequestRepositoryPort;
import ifmo.se.coursach_back.lab.application.ports.LabTestResultRepositoryPort;
import ifmo.se.coursach_back.lab.application.ports.LabTestTypeRepositoryPort;
import ifmo.se.coursach_back.medical.application.ports.MedicalCheckRepositoryPort;
import ifmo.se.coursach_back.medical.application.ports.SampleRepositoryPort;
import ifmo.se.coursach_back.admin.application.ports.StaffProfileRepositoryPort;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class LabWorkflowService {
    private static final List<SampleStatus> DEFAULT_PENDING_STATUSES = List.of(SampleStatus.REGISTERED, SampleStatus.NEW);
    private static final Set<String> ALLOWED_FLAGS = Set.of("OK", "POSITIVE", "NEGATIVE", "INCONCLUSIVE");

    private final SampleRepositoryPort sampleRepository;
    private final LabTestResultRepositoryPort labTestResultRepository;
    private final LabTestTypeRepositoryPort labTestTypeRepository;
    private final StaffProfileRepositoryPort staffProfileRepository;
    private final MedicalCheckRepositoryPort medicalCheckRepository;
    private final LabExaminationRequestRepositoryPort labExaminationRequestRepository;
    private final DonorProfileRepositoryPort donorProfileRepository;

    public List<LabExaminationRequest> listPendingRequests() {
        return labExaminationRequestRepository.findByStatuses(
                List.of(LabExaminationStatus.REQUESTED, LabExaminationStatus.IN_PROGRESS));
    }

    public List<Sample> listPendingSamples(String status) {
        List<SampleStatus> statuses = status == null || status.isBlank()
                ? Arrays.asList(SampleStatus.values())
                : List.of(SampleStatus.valueOf(status.trim().toUpperCase()));
        return sampleRepository.findByStatuses(statuses);
    }

    @Transactional
    public LabTestResult recordResult(UUID accountId, LabTestResultRequest request) {
        StaffProfile staff = requireStaff(accountId);
        Sample sample = sampleRepository.findById(request.sampleId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sample not found"));
        LabTestType testType = labTestTypeRepository.findById(request.testTypeId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Test type not found"));

        String flag = normalizeFlag(request.resultFlag());
        if (!ALLOWED_FLAGS.contains(flag)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid result flag");
        }

        LabTestResult result = labTestResultRepository
                .findBySampleAndTestType(sample.getId(), testType.getId())
                .orElseGet(LabTestResult::new);

        if (result.getId() != null && result.isPublished()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Result is already published");
        }

        result.setSample(sample);
        result.setTestType(testType);
        result.setLabTech(staff);
        result.setResultValue(request.resultValue());
        result.setResultFlag(flag);
        result.setTestedAt(OffsetDateTime.now());

        if (sample.getStatus() == SampleStatus.NEW) {
            sample.setStatus(SampleStatus.REGISTERED);
            sampleRepository.save(sample);
        }

        return labTestResultRepository.save(result);
    }

    @Transactional
    public LabTestResult publishResult(UUID accountId, UUID resultId) {
        requireStaff(accountId);
        LabTestResult result = labTestResultRepository.findById(resultId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Result not found"));
        if (!result.isPublished()) {
            result.setPublished(true);
            result.setPublishedAt(OffsetDateTime.now());
            result = labTestResultRepository.save(result);

            Sample sample = result.getSample();
            if (sample.getStatus() == SampleStatus.NEW || sample.getStatus() == SampleStatus.REGISTERED) {
                sample.setStatus(SampleStatus.PROCESSED);
                sampleRepository.save(sample);
            }
        }
        return result;
    }

    public List<LabTestResult> getResultsBySample(UUID sampleId) {
        Sample sample = sampleRepository.findById(sampleId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sample not found"));
        return labTestResultRepository.findBySampleId(sample.getId());
    }

    @Transactional
    public LabExaminationRequest submitExamination(UUID accountId, UUID requestId,
                                                   ifmo.se.coursach_back.lab.api.dto.LabExaminationRequest request) {
        StaffProfile labTech = requireStaff(accountId);
        LabExaminationRequest requestEntity = labExaminationRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lab request not found"));

        if (LabExaminationStatus.COMPLETED.equals(requestEntity.getStatus())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Lab request already completed");
        }

        if (request.hemoglobinGl() == null || request.hematocritPct() == null || request.rbc10e12L() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "All blood analysis values are required");
        }
        BloodGroup bloodGroup = BloodGroup.fromStringOrNull(request.bloodGroup());
        if (bloodGroup == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Blood group is required and must be valid");
        }
        RhFactor rhFactor = RhFactor.fromStringOrNull(request.rhFactor());
        if (rhFactor == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Rh factor is required and must be valid");
        }

        requestEntity.setCompletedByLab(labTech);
        requestEntity.setCompletedAt(OffsetDateTime.now());
        requestEntity.setStatus(LabExaminationStatus.COMPLETED);
        requestEntity.setHemoglobinGl(request.hemoglobinGl());
        requestEntity.setHematocritPct(request.hematocritPct());
        requestEntity.setRbc10e12L(request.rbc10e12L());
        requestEntity.setBloodGroup(bloodGroup);
        requestEntity.setRhFactor(rhFactor);
        LabExaminationRequest savedRequest = labExaminationRequestRepository.save(requestEntity);

        // Update donor profile with blood group and Rh factor
        DonorProfile donorProfile = requestEntity.getVisit().getBooking().getDonor();
        donorProfile.setBloodGroup(bloodGroup);
        donorProfile.setRhFactor(rhFactor);
        donorProfileRepository.save(donorProfile);

        MedicalCheck check = medicalCheckRepository.findByVisitId(requestEntity.getVisit().getId())
                .orElseGet(MedicalCheck::new);
        check.setVisit(requestEntity.getVisit());
        check.setSubmittedByLab(labTech);
        check.setSubmittedAt(OffsetDateTime.now());
        check.setHemoglobinGl(request.hemoglobinGl());
        check.setHematocritPct(request.hematocritPct());
        check.setRbc10e12L(request.rbc10e12L());
        check.setStatus(MedicalCheckDecision.PENDING_REVIEW);
        check.setDecision(MedicalCheckDecision.PENDING_REVIEW);
        check.setDecisionAt(OffsetDateTime.now());
        
        medicalCheckRepository.save(check);
        return savedRequest;
    }

    private StaffProfile requireStaff(UUID accountId) {
        return staffProfileRepository.findByAccountId(accountId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Staff profile not found"));
    }

    private String normalizeFlag(String value) {
        if (value == null) {
            return "";
        }
        return value.trim().toUpperCase();
    }
}

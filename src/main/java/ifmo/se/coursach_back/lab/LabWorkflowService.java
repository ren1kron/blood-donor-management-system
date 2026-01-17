package ifmo.se.coursach_back.lab;

import ifmo.se.coursach_back.lab.dto.LabTestResultRequest;
import ifmo.se.coursach_back.model.LabTestResult;
import ifmo.se.coursach_back.model.LabTestType;
import ifmo.se.coursach_back.model.Sample;
import ifmo.se.coursach_back.model.StaffProfile;
import ifmo.se.coursach_back.repository.LabTestResultRepository;
import ifmo.se.coursach_back.repository.LabTestTypeRepository;
import ifmo.se.coursach_back.repository.SampleRepository;
import ifmo.se.coursach_back.repository.StaffProfileRepository;
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
    private static final List<String> DEFAULT_PENDING_STATUSES = List.of("REGISTERED", "NEW");
    private static final Set<String> ALLOWED_FLAGS = Set.of("OK", "POSITIVE", "NEGATIVE", "INCONCLUSIVE");

    private final SampleRepository sampleRepository;
    private final LabTestResultRepository labTestResultRepository;
    private final LabTestTypeRepository labTestTypeRepository;
    private final StaffProfileRepository staffProfileRepository;

    public List<Sample> listPendingSamples(String status) {
        List<String> statuses = status == null || status.isBlank()
                ? DEFAULT_PENDING_STATUSES
                : List.of(status.trim().toUpperCase());
        return sampleRepository.findByStatusInOrderByCollectedAtAsc(statuses);
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
                .findBySample_IdAndTestType_Id(sample.getId(), testType.getId())
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
        }
        return result;
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

package ifmo.se.coursach_back.lab;

import ifmo.se.coursach_back.lab.dto.LabExaminationRequest;
import ifmo.se.coursach_back.lab.dto.LabTestResultRequest;
import ifmo.se.coursach_back.model.Booking;
import ifmo.se.coursach_back.model.LabTestResult;
import ifmo.se.coursach_back.model.LabTestType;
import ifmo.se.coursach_back.model.MedicalCheck;
import ifmo.se.coursach_back.model.Sample;
import ifmo.se.coursach_back.model.StaffProfile;
import ifmo.se.coursach_back.model.Visit;
import ifmo.se.coursach_back.repository.BookingRepository;
import ifmo.se.coursach_back.repository.LabTestResultRepository;
import ifmo.se.coursach_back.repository.LabTestTypeRepository;
import ifmo.se.coursach_back.repository.MedicalCheckRepository;
import ifmo.se.coursach_back.repository.SampleRepository;
import ifmo.se.coursach_back.repository.StaffProfileRepository;
import ifmo.se.coursach_back.repository.VisitRepository;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
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
public class LabWorkflowService {
    private static final List<String> DEFAULT_PENDING_STATUSES = List.of("REGISTERED", "NEW");
    private static final Set<String> ALLOWED_FLAGS = Set.of("OK", "POSITIVE", "NEGATIVE", "INCONCLUSIVE");

    private final SampleRepository sampleRepository;
    private final LabTestResultRepository labTestResultRepository;
    private final LabTestTypeRepository labTestTypeRepository;
    private final StaffProfileRepository staffProfileRepository;
    private final MedicalCheckRepository medicalCheckRepository;
    private final VisitRepository visitRepository;
    private final BookingRepository bookingRepository;

    public List<Booking> listAwaitingExamination() {
        OffsetDateTime start = OffsetDateTime.now().minusHours(8);
        List<Booking> bookings = bookingRepository.findExaminationBookingsFrom(start);
        
        Set<String> activeStatuses = Set.of("CONFIRMED", "BOOKED", "CHECKED_IN");
        List<Booking> activeBookings = bookings.stream()
                .filter(b -> activeStatuses.contains(b.getStatus()))
                .toList();
        
        if (activeBookings.isEmpty()) {
            return List.of();
        }
        
        List<UUID> bookingIds = activeBookings.stream().map(Booking::getId).toList();
        List<Visit> visits = visitRepository.findByBooking_IdIn(bookingIds);
        Set<UUID> visitIds = visits.stream().map(Visit::getId).collect(Collectors.toSet());
        
        List<MedicalCheck> existingChecks = medicalCheckRepository.findByVisit_IdIn(visitIds.stream().toList());
        Set<UUID> checkedVisitIds = existingChecks.stream()
                .map(mc -> mc.getVisit().getId())
                .collect(Collectors.toSet());
        
        Map<UUID, Visit> visitByBookingId = visits.stream()
                .collect(Collectors.toMap(v -> v.getBooking().getId(), v -> v));
        
        return activeBookings.stream()
                .filter(b -> {
                    Visit visit = visitByBookingId.get(b.getId());
                    return visit == null || !checkedVisitIds.contains(visit.getId());
                })
                .toList();
    }
    
    public Map<UUID, Visit> loadVisitsByBookingIds(List<UUID> bookingIds) {
        return visitRepository.findByBooking_IdIn(bookingIds).stream()
                .collect(Collectors.toMap(v -> v.getBooking().getId(), v -> v));
    }

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

    public List<LabTestResult> getResultsBySample(UUID sampleId) {
        Sample sample = sampleRepository.findById(sampleId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sample not found"));
        return labTestResultRepository.findBySample_Id(sample.getId());
    }

    /**
     * Lab technician submits examination results for doctor review
     */
    @Transactional
    public MedicalCheck submitExamination(UUID accountId, LabExaminationRequest request) {
        StaffProfile labTech = requireStaff(accountId);
        Visit visit = resolveVisit(request.visitId(), request.bookingId());
        
        // Check if examination already exists
        MedicalCheck check = medicalCheckRepository.findByVisit_Id(visit.getId())
                .orElseGet(MedicalCheck::new);
        
        if (check.getId() != null && !"PENDING_REVIEW".equals(check.getStatus())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Examination already reviewed by doctor");
        }
        
        check.setVisit(visit);
        check.setSubmittedByLab(labTech);
        check.setSubmittedAt(OffsetDateTime.now());
        check.setWeightKg(request.weightKg());
        check.setHemoglobinGl(request.hemoglobinGl());
        check.setSystolicMmhg(request.systolicMmhg());
        check.setDiastolicMmhg(request.diastolicMmhg());
        check.setPulseRate(request.pulseRate());
        check.setBodyTemperatureC(request.bodyTemperatureC());
        check.setStatus("PENDING_REVIEW");
        check.setDecision("PENDING_REVIEW");
        check.setDecisionAt(OffsetDateTime.now());
        
        return medicalCheckRepository.save(check);
    }
    
    public List<MedicalCheck> listPendingExaminations() {
        return medicalCheckRepository.findByStatusOrderBySubmittedAtAsc("PENDING_REVIEW");
    }
    
    private Visit resolveVisit(UUID visitId, UUID bookingId) {
        if (visitId != null) {
            return visitRepository.findById(visitId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Visit not found"));
        }
        if (bookingId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "visitId or bookingId is required");
        }
        
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found"));
        
        return visitRepository.findByBooking_Id(bookingId)
                .orElseGet(() -> {
                    Visit newVisit = new Visit();
                    newVisit.setBooking(booking);
                    return visitRepository.save(newVisit);
                });
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

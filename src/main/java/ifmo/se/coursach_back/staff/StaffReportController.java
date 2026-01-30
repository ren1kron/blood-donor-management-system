package ifmo.se.coursach_back.staff;

import ifmo.se.coursach_back.staff.dto.StaffDonorReport;
import ifmo.se.coursach_back.staff.dto.StaffDonorSummary;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Part C: Staff Reports endpoints.
 * Provides donor listing and detailed donor reports for staff members.
 */
@RestController
@RequestMapping("/api/staff")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('NURSE', 'LAB', 'DOCTOR', 'ADMIN', 'GOD')")
public class StaffReportController {
    private final StaffReportService staffReportService;

    /**
     * GET /api/staff/donors?status=ACTIVE
     * List donors, optionally filtered by status.
     */
    @GetMapping("/donors")
    public ResponseEntity<List<StaffDonorSummary>> listDonors(
            @RequestParam(required = false) String status) {
        List<StaffDonorSummary> donors = staffReportService.listDonors(status);
        return ResponseEntity.ok(donors);
    }

    /**
     * GET /api/staff/donors/{donorId}/report
     * Get detailed donor report.
     */
    @GetMapping("/donors/{donorId}/report")
    public ResponseEntity<StaffDonorReport> getDonorReport(@PathVariable UUID donorId) {
        StaffDonorReport report = staffReportService.getDonorReport(donorId);
        return ResponseEntity.ok(report);
    }
}

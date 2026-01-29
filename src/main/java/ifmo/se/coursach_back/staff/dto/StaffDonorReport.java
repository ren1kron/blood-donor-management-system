package ifmo.se.coursach_back.staff.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Detailed donor report for staff.
 */
public record StaffDonorReport(
        UUID donorId,
        String fullName,
        LocalDate birthDate,
        String bloodGroup,
        String rhFactor,
        String donorStatus,
        String email,
        String phone,
        DonorStats stats,
        List<DonationRecord> donations,
        List<MedicalCheckRecord> medicalChecks
) {
    public record DonorStats(
            int totalDonations,
            int totalVolumeMl,
            OffsetDateTime firstDonationAt,
            OffsetDateTime lastDonationAt,
            OffsetDateTime nextEligibleAt
    ) {}

    public record DonationRecord(
            UUID donationId,
            OffsetDateTime performedAt,
            String donationType,
            Integer volumeMl,
            boolean published
    ) {}

    public record MedicalCheckRecord(
            UUID checkId,
            OffsetDateTime decisionAt,
            String decision,
            String status,
            BigDecimal hemoglobinGl,
            BigDecimal hematocritPct,
            BigDecimal rbc10e12L,
            Integer systolicMmhg,
            Integer diastolicMmhg,
            String doctorName
    ) {}
}

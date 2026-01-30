package ifmo.se.coursach_back.staff;

import ifmo.se.coursach_back.medical.domain.Donation;
import ifmo.se.coursach_back.donor.domain.DonorProfile;
import ifmo.se.coursach_back.donor.domain.DonorStatus;
import ifmo.se.coursach_back.medical.domain.MedicalCheck;
import ifmo.se.coursach_back.medical.infra.jpa.DonationRepository;
import ifmo.se.coursach_back.donor.infra.jpa.DonorProfileRepository;
import ifmo.se.coursach_back.medical.infra.jpa.MedicalCheckRepository;
import ifmo.se.coursach_back.staff.dto.StaffDonorReport;
import ifmo.se.coursach_back.staff.dto.StaffDonorSummary;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class StaffReportService {
    private static final int DONATION_COOLDOWN_DAYS = 56; // 8 weeks between donations

    private final DonorProfileRepository donorProfileRepository;
    private final DonationRepository donationRepository;
    private final MedicalCheckRepository medicalCheckRepository;

    /**
     * List donors filtered by status.
     */
    public List<StaffDonorSummary> listDonors(String status) {
        List<DonorProfile> donors;
        if (status != null && !status.isBlank()) {
            try {
                DonorStatus donorStatus = DonorStatus.valueOf(status.toUpperCase());
                donors = donorProfileRepository.findByDonorStatus(donorStatus);
            } catch (IllegalArgumentException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid donor status: " + status);
            }
        } else {
            donors = donorProfileRepository.findAll();
        }

        return donors.stream()
                .map(this::toSummary)
                .toList();
    }

    /**
     * Get detailed donor report.
     */
    public StaffDonorReport getDonorReport(UUID donorId) {
        DonorProfile donor = donorProfileRepository.findById(donorId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Donor not found"));

        List<Donation> donations = donationRepository.findByDonorAccountId(donor.getAccount().getId());
        List<MedicalCheck> checks = medicalCheckRepository.findByDonorId(donor.getId());

        return buildReport(donor, donations, checks);
    }

    private StaffDonorSummary toSummary(DonorProfile donor) {
        List<Donation> donations = donationRepository.findByDonorAccountId(donor.getAccount().getId());
        OffsetDateTime lastDonation = donations.stream()
                .map(Donation::getPerformedAt)
                .max(OffsetDateTime::compareTo)
                .orElse(null);

        return new StaffDonorSummary(
                donor.getId(),
                donor.getFullName(),
                donor.getBloodGroup() != null ? donor.getBloodGroup().getDisplayValue() : null,
                donor.getRhFactor() != null ? donor.getRhFactor().getDisplayValue() : null,
                donor.getDonorStatus(),
                donor.getAccount().getEmail(),
                donor.getAccount().getPhone(),
                lastDonation,
                donations.size()
        );
    }

    private StaffDonorReport buildReport(DonorProfile donor, List<Donation> donations, List<MedicalCheck> checks) {
        OffsetDateTime firstDonation = donations.stream()
                .map(Donation::getPerformedAt)
                .min(OffsetDateTime::compareTo)
                .orElse(null);
        OffsetDateTime lastDonation = donations.stream()
                .map(Donation::getPerformedAt)
                .max(OffsetDateTime::compareTo)
                .orElse(null);

        int totalVolume = donations.stream()
                .mapToInt(d -> d.getVolumeMl() != null ? d.getVolumeMl() : 0)
                .sum();

        OffsetDateTime nextEligible = lastDonation != null
                ? lastDonation.plusDays(DONATION_COOLDOWN_DAYS)
                : OffsetDateTime.now();

        StaffDonorReport.DonorStats stats = new StaffDonorReport.DonorStats(
                donations.size(),
                totalVolume,
                firstDonation,
                lastDonation,
                nextEligible
        );

        List<StaffDonorReport.DonationRecord> donationRecords = donations.stream()
                .map(d -> new StaffDonorReport.DonationRecord(
                        d.getId(),
                        d.getPerformedAt(),
                        d.getDonationType() != null ? d.getDonationType().getValue() : null,
                        d.getVolumeMl(),
                        d.isPublished()
                ))
                .toList();

        List<StaffDonorReport.MedicalCheckRecord> checkRecords = checks.stream()
                .map(c -> new StaffDonorReport.MedicalCheckRecord(
                        c.getId(),
                        c.getDecisionAt(),
                        c.getDecision(),
                        c.getStatus(),
                        c.getHemoglobinGl(),
                        c.getHematocritPct(),
                        c.getRbc10e12L(),
                        c.getSystolicMmhg(),
                        c.getDiastolicMmhg(),
                        c.getPerformedBy() != null ? c.getPerformedBy().getFullName() : null
                ))
                .toList();

        return new StaffDonorReport(
                donor.getId(),
                donor.getFullName(),
                donor.getBirthDate(),
                donor.getBloodGroup() != null ? donor.getBloodGroup().getDisplayValue() : null,
                donor.getRhFactor() != null ? donor.getRhFactor().getDisplayValue() : null,
                donor.getDonorStatus(),
                donor.getAccount().getEmail(),
                donor.getAccount().getPhone(),
                stats,
                donationRecords,
                checkRecords
        );
    }
}

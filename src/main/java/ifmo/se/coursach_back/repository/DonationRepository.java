package ifmo.se.coursach_back.repository;

import ifmo.se.coursach_back.admin.dto.EligibleDonorProjection;
import ifmo.se.coursach_back.donor.dto.DonationHistoryProjection;
import ifmo.se.coursach_back.model.Donation;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DonationRepository extends JpaRepository<Donation, UUID> {
    Optional<Donation> findByVisit_Id(UUID visitId);

    @Query(value = """
            select
                donor_id as donorId,
                full_name as fullName,
                phone,
                email,
                last_donation_at as lastDonationAt
            from fn_eligible_donors(:threshold)
            """, nativeQuery = true)
    List<EligibleDonorProjection> findEligibleDonors(@Param("threshold") OffsetDateTime threshold);

    @Query(value = """
            select
                donation_id as donationId,
                visit_id as visitId,
                performed_at as performedAt,
                donation_type as donationType,
                volume_ml as volumeMl
            from fn_donor_donations(:accountId)
            """, nativeQuery = true)
    List<DonationHistoryProjection> findDonorDonations(@Param("accountId") UUID accountId);

    java.util.Optional<Donation> findTopByVisit_Booking_Donor_Account_IdOrderByPerformedAtDesc(UUID accountId);
}

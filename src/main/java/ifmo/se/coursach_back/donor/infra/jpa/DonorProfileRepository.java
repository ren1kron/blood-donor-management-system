package ifmo.se.coursach_back.donor.infra.jpa;

import ifmo.se.coursach_back.donor.domain.DonorProfile;
import ifmo.se.coursach_back.shared.infra.jpa.projection.DonorSummaryProjection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface DonorProfileRepository extends JpaRepository<DonorProfile, UUID> {
    Optional<DonorProfile> findByAccountId(UUID accountId);
    
    long countByDonorStatus(String donorStatus);
    
    List<DonorProfile> findByDonorStatus(String donorStatus);

    /**
     * Returns donor summaries using interface projection.
     * More efficient than loading full entities.
     */
    @Query(value = """
            SELECT 
                donor.id as donorId,
                donor.full_name as fullName,
                donor.donor_status as donorStatus,
                account.email as email,
                account.phone as phone,
                (SELECT MAX(donation.performed_at)
                 FROM donation
                 WHERE donation.visit_id IN (
                     SELECT v.id FROM visit v
                     JOIN booking b ON v.booking_id = b.id
                     WHERE b.donor_id = donor.id
                 )) as lastDonationAt,
                (SELECT MAX(mc.decision_at)
                 FROM medical_check mc
                 WHERE mc.decision = 'ADMITTED'
                   AND mc.visit_id IN (
                     SELECT v.id FROM visit v
                     JOIN booking b ON v.booking_id = b.id
                     WHERE b.donor_id = donor.id
                 )) as lastAdmittedAt
            FROM donor_profile donor
            JOIN account account ON donor.account_id = account.id
            ORDER BY donor.full_name
            """, nativeQuery = true)
    List<DonorSummaryProjection> findDonorSummaries();
}

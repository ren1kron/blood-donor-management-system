package ifmo.se.coursach_back.repository;

import ifmo.se.coursach_back.model.DonorProfile;
import ifmo.se.coursach_back.admin.dto.AdminDonorSummaryRow;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface DonorProfileRepository extends JpaRepository<DonorProfile, UUID> {
    Optional<DonorProfile> findByAccountId(UUID accountId);
    
    long countByDonorStatus(String donorStatus);
    
    List<DonorProfile> findByDonorStatus(String donorStatus);

    @Query("""
            select new ifmo.se.coursach_back.admin.dto.AdminDonorSummaryRow(
                donor.id,
                donor.fullName,
                donor.donorStatus,
                account.email,
                account.phone,
                (select max(donation.performedAt)
                 from Donation donation
                 where donation.visit.booking.donor = donor),
                (select max(check.decisionAt)
                 from MedicalCheck check
                 where check.visit.booking.donor = donor
                   and check.decision = 'ADMITTED')
            )
            from DonorProfile donor
            join donor.account account
            order by donor.fullName
            """)
    List<AdminDonorSummaryRow> findDonorSummaries();
}

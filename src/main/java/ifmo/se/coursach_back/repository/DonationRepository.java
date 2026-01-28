package ifmo.se.coursach_back.repository;

import ifmo.se.coursach_back.admin.dto.EligibleDonorRow;
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

    @Query("""
            select new ifmo.se.coursach_back.admin.dto.EligibleDonorRow(
                donor.id,
                donor.fullName,
                account.phone,
                account.email,
                max(donation.performedAt)
            )
            from Donation donation
            join donation.visit visit
            join visit.booking booking
            join booking.donor donor
            join donor.account account
            group by donor.id, donor.fullName, account.phone, account.email
            having max(donation.performedAt) <= :threshold
            order by max(donation.performedAt) asc
            """)
    List<EligibleDonorRow> findEligibleDonors(@Param("threshold") OffsetDateTime threshold);

    @Query("""
            select donation
            from Donation donation
            join donation.visit visit
            join visit.booking booking
            join booking.donor donor
            where donor.account.id = :accountId
            order by donation.performedAt desc
            """)
    List<Donation> findByDonorAccountId(@Param("accountId") UUID accountId);

    java.util.Optional<Donation> findTopByVisit_Booking_Donor_Account_IdOrderByPerformedAtDesc(UUID accountId);
    
    List<Donation> findByVisit_IdIn(List<UUID> visitIds);
    
    @Query("SELECT COUNT(d) FROM Donation d WHERE d.performedAt >= :from AND d.performedAt <= :to")
    long countByPerformedAtBetween(@Param("from") OffsetDateTime from, @Param("to") OffsetDateTime to);
    
    @Query("""
            SELECT donor.bloodGroup, donor.rhFactor, SUM(d.volumeMl)
            FROM Donation d
            JOIN d.visit v
            JOIN v.booking b
            JOIN b.donor donor
            WHERE d.performedAt >= :from AND d.performedAt <= :to
            GROUP BY donor.bloodGroup, donor.rhFactor
            """)
    List<Object[]> sumVolumeByBloodTypeAndRh(@Param("from") OffsetDateTime from, @Param("to") OffsetDateTime to);
}

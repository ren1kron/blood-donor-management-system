package ifmo.se.coursach_back.medical.infra.jpa;

import ifmo.se.coursach_back.admin.api.dto.EligibleDonorRow;
import ifmo.se.coursach_back.medical.domain.Donation;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DonationRepository extends JpaRepository<Donation, UUID> {
    @Query("select d from Donation d where d.visit.id = :visitId")
    Optional<Donation> findByVisitId(@Param("visitId") UUID visitId);

    @Query("""
            select new ifmo.se.coursach_back.admin.api.dto.EligibleDonorRow(
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

    @Query("""
            select donation
            from Donation donation
            join donation.visit visit
            join visit.booking booking
            join booking.donor donor
            where donor.account.id = :accountId
              and donation.published = true
            order by donation.performedAt desc
            """)
    List<Donation> findPublishedByDonorAccountId(@Param("accountId") UUID accountId);

    @Query("""
            select d
            from Donation d
            join d.visit v
            join v.booking b
            join b.donor donor
            where donor.account.id = :accountId
            order by d.performedAt desc
            """)
    List<Donation> findLatestByDonorAccountId(@Param("accountId") UUID accountId, Pageable pageable);

    default Optional<Donation> findLatestByDonorAccountId(UUID accountId) {
        return findLatestByDonorAccountId(accountId, PageRequest.of(0, 1))
                .stream()
                .findFirst();
    }
    
    @Query("select d from Donation d where d.visit.id in :visitIds")
    List<Donation> findByVisitIds(@Param("visitIds") List<UUID> visitIds);
    
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

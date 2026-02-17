package ifmo.se.coursach_back.medical.infra.jpa;

import ifmo.se.coursach_back.medical.domain.BloodUnit;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BloodUnitRepository extends JpaRepository<BloodUnit, UUID> {
    @Query("SELECT bu FROM BloodUnit bu WHERE bu.status IN :statuses")
    List<BloodUnit> findByStatuses(@Param("statuses") List<String> statuses);

    @Query("SELECT bu FROM BloodUnit bu WHERE bu.donation.id = :donationId")
    List<BloodUnit> findByDonationId(@Param("donationId") UUID donationId);
}

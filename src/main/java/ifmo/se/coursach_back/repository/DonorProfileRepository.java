package ifmo.se.coursach_back.repository;

import ifmo.se.coursach_back.model.DonorProfile;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DonorProfileRepository extends JpaRepository<DonorProfile, UUID> {
    Optional<DonorProfile> findByAccountId(UUID accountId);
    
    long countByDonorStatus(String donorStatus);
    
    List<DonorProfile> findByDonorStatus(String donorStatus);
}

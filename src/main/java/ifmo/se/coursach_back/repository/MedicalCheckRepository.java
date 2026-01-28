package ifmo.se.coursach_back.repository;

import ifmo.se.coursach_back.model.MedicalCheck;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicalCheckRepository extends JpaRepository<MedicalCheck, UUID> {
    Optional<MedicalCheck> findByVisit_Id(UUID visitId);
    
    List<MedicalCheck> findByVisit_IdIn(List<UUID> visitIds);
}

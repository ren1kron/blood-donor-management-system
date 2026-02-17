package ifmo.se.coursach_back.medical.infra.jpa;

import ifmo.se.coursach_back.medical.domain.BloodComponentType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BloodComponentTypeRepository extends JpaRepository<BloodComponentType, Short> {
}

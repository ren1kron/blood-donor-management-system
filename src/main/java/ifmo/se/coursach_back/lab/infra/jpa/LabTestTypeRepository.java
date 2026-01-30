package ifmo.se.coursach_back.lab.infra.jpa;

import ifmo.se.coursach_back.lab.domain.LabTestType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LabTestTypeRepository extends JpaRepository<LabTestType, Short> {
}

package ifmo.se.coursach_back.lab.application.ports;

import ifmo.se.coursach_back.lab.domain.LabTestType;
import java.util.List;
import java.util.Optional;

/**
 * Port interface for LabTestType repository operations.
 * Application layer depends on this interface, not on JPA repository.
 */
public interface LabTestTypeRepositoryPort {
    Optional<LabTestType> findById(Short id);
    List<LabTestType> findAll();
}

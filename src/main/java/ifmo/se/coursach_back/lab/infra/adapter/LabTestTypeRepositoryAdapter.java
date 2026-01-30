package ifmo.se.coursach_back.lab.infra.adapter;

import ifmo.se.coursach_back.lab.application.ports.LabTestTypeRepositoryPort;
import ifmo.se.coursach_back.lab.domain.LabTestType;
import ifmo.se.coursach_back.lab.infra.jpa.LabTestTypeRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class LabTestTypeRepositoryAdapter implements LabTestTypeRepositoryPort {
    private final LabTestTypeRepository jpaRepository;

    @Override
    public Optional<LabTestType> findById(Short id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<LabTestType> findAll() {
        return jpaRepository.findAll();
    }
}

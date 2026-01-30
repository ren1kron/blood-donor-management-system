package ifmo.se.coursach_back.admin.infra.adapter;

import ifmo.se.coursach_back.admin.application.ports.StaffProfileRepositoryPort;
import ifmo.se.coursach_back.admin.domain.StaffProfile;
import ifmo.se.coursach_back.admin.infra.jpa.StaffProfileRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class StaffProfileRepositoryAdapter implements StaffProfileRepositoryPort {
    private final StaffProfileRepository jpaRepository;

    @Override
    public Optional<StaffProfile> findById(UUID id) {
        return jpaRepository.findById(id);
    }

    @Override
    public Optional<StaffProfile> findByAccountId(UUID accountId) {
        return jpaRepository.findByAccountId(accountId);
    }

    @Override
    public List<StaffProfile> findAll() {
        return jpaRepository.findAll();
    }

    @Override
    public StaffProfile save(StaffProfile staffProfile) {
        return jpaRepository.save(staffProfile);
    }
}

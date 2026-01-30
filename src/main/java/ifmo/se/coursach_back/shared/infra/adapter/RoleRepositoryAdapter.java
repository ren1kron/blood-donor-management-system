package ifmo.se.coursach_back.shared.infra.adapter;

import ifmo.se.coursach_back.shared.application.ports.RoleRepositoryPort;
import ifmo.se.coursach_back.shared.domain.Role;
import ifmo.se.coursach_back.shared.infra.jpa.RoleRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RoleRepositoryAdapter implements RoleRepositoryPort {
    private final RoleRepository jpaRepository;

    @Override
    public Optional<Role> findById(Short id) {
        return jpaRepository.findById(id);
    }

    @Override
    public Optional<Role> findByCode(String code) {
        return jpaRepository.findByCode(code);
    }
}

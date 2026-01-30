package ifmo.se.coursach_back.shared.infra.jpa;

import ifmo.se.coursach_back.shared.domain.Role;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Short> {
    Optional<Role> findByCode(String code);
}

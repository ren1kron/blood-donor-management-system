package ifmo.se.coursach_back.shared.infra.jpa;

import ifmo.se.coursach_back.shared.domain.Account;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, UUID> {
    Optional<Account> findByEmailIgnoreCase(String email);

    Optional<Account> findByPhone(String phone);

    Optional<Account> findByEmailIgnoreCaseOrPhone(String email, String phone);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByPhone(String phone);

    boolean existsByEmailIgnoreCaseAndIdNot(String email, java.util.UUID id);

    boolean existsByPhoneAndIdNot(String phone, java.util.UUID id);
}

package ifmo.se.coursach_back.repository;

import ifmo.se.coursach_back.model.Account;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, UUID> {
    Optional<Account> findByEmailIgnoreCase(String email);

    Optional<Account> findByPhone(String phone);

    Optional<Account> findByEmailIgnoreCaseOrPhone(String email, String phone);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByPhone(String phone);
}

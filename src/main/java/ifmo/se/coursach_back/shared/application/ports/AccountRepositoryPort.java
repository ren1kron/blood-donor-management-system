package ifmo.se.coursach_back.shared.application.ports;

import ifmo.se.coursach_back.shared.domain.Account;
import java.util.Optional;
import java.util.UUID;

/**
 * Port interface for Account repository operations.
 * Application layer depends on this interface, not on JPA repository.
 */
public interface AccountRepositoryPort {
    Optional<Account> findById(UUID id);
    Optional<Account> findByEmailIgnoreCase(String email);
    Optional<Account> findByPhone(String phone);
    Optional<Account> findByEmailIgnoreCaseOrPhone(String email, String phone);
    boolean existsByEmailIgnoreCase(String email);
    boolean existsByPhone(String phone);
    boolean existsByEmailIgnoreCaseAndIdNot(String email, UUID id);
    boolean existsByPhoneAndIdNot(String phone, UUID id);
    Account save(Account account);
}

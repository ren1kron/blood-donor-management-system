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
    
    /**
     * Find account by email with roles eagerly loaded.
     * Use this method when roles are needed (e.g., during authentication).
     */
    Optional<Account> findByEmailWithRoles(String email);
    
    /**
     * Find account by phone with roles eagerly loaded.
     * Use this method when roles are needed (e.g., during authentication).
     */
    Optional<Account> findByPhoneWithRoles(String phone);
    
    /**
     * Find account by id with roles eagerly loaded.
     */
    Optional<Account> findByIdWithRoles(UUID id);
    
    /**
     * Find account by email or phone with roles eagerly loaded.
     * Use this method for login when roles are needed.
     */
    Optional<Account> findByEmailIgnoreCaseOrPhoneWithRoles(String identifier);
    
    boolean existsByEmailIgnoreCase(String email);
    boolean existsByPhone(String phone);
    boolean existsByEmailIgnoreCaseAndIdNot(String email, UUID id);
    boolean existsByPhoneAndIdNot(String phone, UUID id);
    Account save(Account account);
}

package ifmo.se.coursach_back.shared.infra.jpa;

import ifmo.se.coursach_back.shared.domain.Account;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AccountRepository extends JpaRepository<Account, UUID> {
    Optional<Account> findByEmailIgnoreCase(String email);

    Optional<Account> findByPhone(String phone);

    /**
     * Find account by email with roles eagerly loaded.
     * Use this method when roles are needed (e.g., during authentication).
     */
    @EntityGraph(attributePaths = "roles")
    @Query("SELECT a FROM Account a WHERE LOWER(a.email) = LOWER(:email)")
    Optional<Account> findByEmailWithRoles(@Param("email") String email);

    /**
     * Find account by phone with roles eagerly loaded.
     * Use this method when roles are needed (e.g., during authentication).
     */
    @EntityGraph(attributePaths = "roles")
    @Query("SELECT a FROM Account a WHERE a.phone = :phone")
    Optional<Account> findByPhoneWithRoles(@Param("phone") String phone);

    /**
     * Find account by id with roles eagerly loaded.
     */
    @EntityGraph(attributePaths = "roles")
    @Query("SELECT a FROM Account a WHERE a.id = :id")
    Optional<Account> findByIdWithRoles(@Param("id") UUID id);

    /**
     * Find account by email or phone with roles eagerly loaded.
     * Use this method for login when roles are needed.
     */
    @EntityGraph(attributePaths = "roles")
    @Query("SELECT a FROM Account a WHERE LOWER(a.email) = LOWER(:identifier) OR a.phone = :identifier")
    Optional<Account> findByEmailIgnoreCaseOrPhoneWithRoles(@Param("identifier") String identifier);

    Optional<Account> findByEmailIgnoreCaseOrPhone(String email, String phone);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByPhone(String phone);

    boolean existsByEmailIgnoreCaseAndIdNot(String email, java.util.UUID id);

    boolean existsByPhoneAndIdNot(String phone, java.util.UUID id);
}

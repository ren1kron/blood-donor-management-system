package ifmo.se.coursach_back.shared.infra.adapter;

import ifmo.se.coursach_back.shared.application.ports.AccountRepositoryPort;
import ifmo.se.coursach_back.shared.domain.Account;
import ifmo.se.coursach_back.shared.infra.jpa.AccountRepository;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class AccountRepositoryAdapter implements AccountRepositoryPort {
    private final AccountRepository jpaRepository;

    @Override
    public Optional<Account> findById(UUID id) {
        return jpaRepository.findById(id);
    }

    @Override
    public Optional<Account> findByEmailIgnoreCase(String email) {
        return jpaRepository.findByEmailIgnoreCase(email);
    }

    @Override
    public Optional<Account> findByPhone(String phone) {
        return jpaRepository.findByPhone(phone);
    }

    @Override
    public Optional<Account> findByEmailIgnoreCaseOrPhone(String email, String phone) {
        return jpaRepository.findByEmailIgnoreCaseOrPhone(email, phone);
    }

    @Override
    public Optional<Account> findByEmailWithRoles(String email) {
        return jpaRepository.findByEmailWithRoles(email);
    }

    @Override
    public Optional<Account> findByPhoneWithRoles(String phone) {
        return jpaRepository.findByPhoneWithRoles(phone);
    }

    @Override
    public Optional<Account> findByIdWithRoles(UUID id) {
        return jpaRepository.findByIdWithRoles(id);
    }

    @Override
    public Optional<Account> findByEmailIgnoreCaseOrPhoneWithRoles(String identifier) {
        return jpaRepository.findByEmailIgnoreCaseOrPhoneWithRoles(identifier);
    }

    @Override
    public boolean existsByEmailIgnoreCase(String email) {
        return jpaRepository.existsByEmailIgnoreCase(email);
    }

    @Override
    public boolean existsByPhone(String phone) {
        return jpaRepository.existsByPhone(phone);
    }

    @Override
    public boolean existsByEmailIgnoreCaseAndIdNot(String email, UUID id) {
        return jpaRepository.existsByEmailIgnoreCaseAndIdNot(email, id);
    }

    @Override
    public boolean existsByPhoneAndIdNot(String phone, UUID id) {
        return jpaRepository.existsByPhoneAndIdNot(phone, id);
    }

    @Override
    public Account save(Account account) {
        return jpaRepository.save(account);
    }
}

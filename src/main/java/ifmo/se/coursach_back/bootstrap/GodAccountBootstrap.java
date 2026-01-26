package ifmo.se.coursach_back.bootstrap;

import ifmo.se.coursach_back.model.Account;
import ifmo.se.coursach_back.model.Role;
import ifmo.se.coursach_back.repository.AccountRepository;
import ifmo.se.coursach_back.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class GodAccountBootstrap implements ApplicationRunner {
    private static final String GOD_EMAIL = "god@system.local";
    private static final String GOD_PASSWORD = "big_papa";
    private static final String GOD_ROLE = "GOD";
    private static final String GOD_FULL_NAME = "System Administrator";

    private final AccountRepository accountRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        Role godRole = roleRepository.findByCode(GOD_ROLE).orElse(null);
        if (godRole == null) {
            log.warn("Role {} not found; skipping GOD account bootstrap.", GOD_ROLE);
            return;
        }

        Account account = accountRepository.findByEmailIgnoreCase(GOD_EMAIL).orElse(null);
        if (account == null) {
            account = new Account();
            account.setEmail(GOD_EMAIL);
            account.setPasswordHash(passwordEncoder.encode(GOD_PASSWORD));
            account.setFullName(GOD_FULL_NAME);
            account.getRoles().add(godRole);
            accountRepository.save(account);
            log.info("Created GOD account with email {}", GOD_EMAIL);
            return;
        }

        boolean hasGodRole = account.getRoles().stream()
                .anyMatch(role -> GOD_ROLE.equals(role.getCode()));
        if (!hasGodRole) {
            account.getRoles().add(godRole);
            accountRepository.save(account);
            log.info("Granted GOD role to account {}", GOD_EMAIL);
        }
    }
}

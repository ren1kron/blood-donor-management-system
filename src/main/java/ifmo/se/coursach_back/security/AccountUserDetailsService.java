package ifmo.se.coursach_back.security;

import ifmo.se.coursach_back.shared.domain.Account;
import ifmo.se.coursach_back.shared.infra.jpa.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * UserDetailsService implementation that loads accounts with roles.
 * Uses EntityGraph to eagerly fetch roles in a single query for authentication.
 */
@Service
@RequiredArgsConstructor
public class AccountUserDetailsService implements UserDetailsService {
    private final AccountRepository accountRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) {
        // Use methods with EntityGraph to load roles eagerly
        Account account = accountRepository.findByEmailWithRoles(username)
                .or(() -> accountRepository.findByPhoneWithRoles(username))
                .orElseThrow(() -> new UsernameNotFoundException("Account not found"));
        return AccountPrincipal.from(account);
    }
}

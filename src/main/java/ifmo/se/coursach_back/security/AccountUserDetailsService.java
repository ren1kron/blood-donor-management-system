package ifmo.se.coursach_back.security;

import ifmo.se.coursach_back.model.Account;
import ifmo.se.coursach_back.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountUserDetailsService implements UserDetailsService {
    private final AccountRepository accountRepository;

    @Override
    public UserDetails loadUserByUsername(String username) {
        Account account = accountRepository.findByEmailIgnoreCase(username)
                .or(() -> accountRepository.findByPhone(username))
                .orElseThrow(() -> new UsernameNotFoundException("Account not found"));
        return AccountPrincipal.from(account);
    }
}

package ifmo.se.coursach_back.security;

import ifmo.se.coursach_back.model.Account;
import ifmo.se.coursach_back.model.Role;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class AccountPrincipal implements UserDetails {
    @Getter
    private final UUID id;
    private final String email;
    private final String phone;
    private final String passwordHash;
    private final boolean active;
    private final Collection<? extends GrantedAuthority> authorities;

    public AccountPrincipal(UUID id, String email, String phone, String passwordHash, boolean active,
                            Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.email = email;
        this.phone = phone;
        this.passwordHash = passwordHash;
        this.active = active;
        this.authorities = authorities;
    }

    public static AccountPrincipal from(Account account) {
        List<SimpleGrantedAuthority> authorities = account.getRoles().stream()
                .map(Role::getCode)
                .map(code -> new SimpleGrantedAuthority("ROLE_" + code))
                .toList();
        return new AccountPrincipal(account.getId(), account.getEmail(), account.getPhone(),
                account.getPasswordHash(), account.isActive(), authorities);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return passwordHash;
    }

    @Override
    public String getUsername() {
        if (email != null) {
            return email;
        }
        if (phone != null) {
            return phone;
        }
        return id != null ? id.toString() : "";
    }

    @Override
    public boolean isAccountNonExpired() {
        return active;
    }

    @Override
    public boolean isAccountNonLocked() {
        return active;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return active;
    }

    @Override
    public boolean isEnabled() {
        return active;
    }
}

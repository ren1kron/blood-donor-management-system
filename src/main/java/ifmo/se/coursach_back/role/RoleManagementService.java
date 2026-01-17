package ifmo.se.coursach_back.role;

import ifmo.se.coursach_back.model.Account;
import ifmo.se.coursach_back.model.Role;
import ifmo.se.coursach_back.repository.AccountRepository;
import ifmo.se.coursach_back.repository.RoleRepository;
import ifmo.se.coursach_back.role.dto.AccountRolesResponse;
import ifmo.se.coursach_back.role.dto.RoleResponse;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class RoleManagementService {
    private final AccountRepository accountRepository;
    private final RoleRepository roleRepository;

    public List<RoleResponse> listRoles() {
        return roleRepository.findAll().stream()
                .sorted(Comparator.comparing(Role::getCode))
                .map(role -> new RoleResponse(role.getCode(), role.getName()))
                .toList();
    }

    public AccountRolesResponse getAccountRoles(UUID accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found"));
        return toResponse(account);
    }

    @Transactional
    public AccountRolesResponse assignRole(UUID accountId, String roleCode) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found"));
        Role role = findRole(roleCode);
        account.getRoles().add(role);
        accountRepository.save(account);
        return toResponse(account);
    }

    @Transactional
    public AccountRolesResponse removeRole(UUID accountId, String roleCode) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found"));
        Role role = findRole(roleCode);
        boolean removed = account.getRoles().remove(role);
        if (!removed) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Role not assigned to account");
        }
        accountRepository.save(account);
        return toResponse(account);
    }

    private Role findRole(String roleCode) {
        String normalized = normalizeRoleCode(roleCode);
        if (normalized == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "roleCode is required");
        }
        return roleRepository.findByCode(normalized)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Role not found"));
    }

    private AccountRolesResponse toResponse(Account account) {
        List<String> roles = account.getRoles().stream()
                .map(Role::getCode)
                .sorted()
                .toList();
        return new AccountRolesResponse(account.getId(), roles);
    }

    private String normalizeRoleCode(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            return null;
        }
        return trimmed.toUpperCase(Locale.ROOT);
    }
}

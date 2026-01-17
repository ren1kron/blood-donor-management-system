package ifmo.se.coursach_back.role;

import ifmo.se.coursach_back.role.dto.AccountRolesResponse;
import ifmo.se.coursach_back.role.dto.RoleAssignmentRequest;
import ifmo.se.coursach_back.role.dto.RoleResponse;
import ifmo.se.coursach_back.security.AccountPrincipal;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN') or hasRole('GOD')")
public class RoleManagementController {
    private final RoleManagementService roleManagementService;

    @GetMapping
    public List<RoleResponse> listRoles() {
        return roleManagementService.listRoles();
    }

    @GetMapping("/accounts/{accountId}")
    public AccountRolesResponse getAccountRoles(@PathVariable UUID accountId) {
        return roleManagementService.getAccountRoles(accountId);
    }

    @PostMapping("/assign")
    public ResponseEntity<AccountRolesResponse> assignRole(
            @AuthenticationPrincipal AccountPrincipal principal,
            @Valid @RequestBody RoleAssignmentRequest request) {
        enforceGodOnlyForGodRole(principal, request.roleCode());
        AccountRolesResponse response = roleManagementService.assignRole(request.accountId(), request.roleCode());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/remove")
    public AccountRolesResponse removeRole(
            @AuthenticationPrincipal AccountPrincipal principal,
            @Valid @RequestBody RoleAssignmentRequest request) {
        enforceGodOnlyForGodRole(principal, request.roleCode());
        return roleManagementService.removeRole(request.accountId(), request.roleCode());
    }

    private void enforceGodOnlyForGodRole(AccountPrincipal principal, String roleCode) {
        if (roleCode == null) {
            return;
        }
        String normalized = roleCode.trim().toUpperCase(Locale.ROOT);
        if (!"GOD".equals(normalized)) {
            return;
        }
        boolean isGod = principal.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_GOD".equals(authority.getAuthority()));
        if (!isGod) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only GOD can manage GOD role");
        }
    }
}

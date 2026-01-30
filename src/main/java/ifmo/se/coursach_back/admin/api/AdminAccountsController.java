package ifmo.se.coursach_back.admin.api;
import ifmo.se.coursach_back.admin.application.AdminAccountService;

import ifmo.se.coursach_back.admin.api.dto.AdminAssignRolesRequest;
import ifmo.se.coursach_back.admin.api.dto.AdminCreateAccountRequest;
import ifmo.se.coursach_back.admin.api.dto.AdminCreateAccountResponse;
import ifmo.se.coursach_back.admin.api.dto.AdminCreateStaffProfileRequest;
import ifmo.se.coursach_back.admin.api.dto.AdminCreateStaffProfileResponse;
import ifmo.se.coursach_back.admin.api.dto.AdminDonorSummaryResponse;
import ifmo.se.coursach_back.admin.api.dto.AdminStaffSummaryResponse;
import ifmo.se.coursach_back.admin.api.dto.AdminUpdateAccountRequest;
import ifmo.se.coursach_back.security.AccountPrincipal;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminAccountsController {
    private final AdminAccountService adminAccountService;

    @PostMapping("/accounts")
    public ResponseEntity<AdminCreateAccountResponse> createAccount(
            @AuthenticationPrincipal AccountPrincipal principal,
            @Valid @RequestBody AdminCreateAccountRequest request) {
        AdminCreateAccountResponse response = adminAccountService.createAccount(principal.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/staff-profiles")
    public ResponseEntity<AdminCreateStaffProfileResponse> createStaffProfile(
            @AuthenticationPrincipal AccountPrincipal principal,
            @Valid @RequestBody AdminCreateStaffProfileRequest request) {
        AdminCreateStaffProfileResponse response = adminAccountService.createStaffProfile(principal.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/accounts/{accountId}/roles")
    public ResponseEntity<AdminStaffSummaryResponse> assignRoles(
            @AuthenticationPrincipal AccountPrincipal principal,
            @PathVariable UUID accountId,
            @Valid @RequestBody AdminAssignRolesRequest request) {
        AdminStaffSummaryResponse response = adminAccountService.assignRoles(principal.getId(), accountId, request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/accounts/{accountId}")
    public ResponseEntity<Void> updateAccount(
            @AuthenticationPrincipal AccountPrincipal principal,
            @PathVariable UUID accountId,
            @RequestBody AdminUpdateAccountRequest request) {
        adminAccountService.updateAccount(principal.getId(), accountId, request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/staff")
    public List<AdminStaffSummaryResponse> listStaff(
            @RequestParam(value = "role", required = false) String role,
            @RequestParam(value = "staffKind", required = false) String staffKind) {
        return adminAccountService.listStaff(role, staffKind);
    }

    @GetMapping("/donors")
    public List<AdminDonorSummaryResponse> listDonors() {
        return adminAccountService.listDonors();
    }
}

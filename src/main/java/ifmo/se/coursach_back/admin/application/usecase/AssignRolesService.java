package ifmo.se.coursach_back.admin.application.usecase;

import ifmo.se.coursach_back.admin.api.dto.AdminAssignRolesRequest;
import ifmo.se.coursach_back.admin.api.dto.AdminStaffSummaryResponse;
import ifmo.se.coursach_back.admin.application.AdminAccountService;
import ifmo.se.coursach_back.admin.application.command.AssignRolesCommand;
import ifmo.se.coursach_back.admin.application.result.StaffSummaryResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Implementation of AssignRolesUseCase that delegates to AdminAccountService.
 */
@Service
@RequiredArgsConstructor
public class AssignRolesService implements AssignRolesUseCase {
    private final AdminAccountService adminAccountService;

    @Override
    public StaffSummaryResult execute(AssignRolesCommand command) {
        AdminAssignRolesRequest request = new AdminAssignRolesRequest(command.roles());
        AdminStaffSummaryResponse response = adminAccountService.assignRoles(
                command.adminAccountId(),
                command.accountId(),
                request
        );
        return new StaffSummaryResult(
                response.staffId(),
                response.accountId(),
                response.fullName(),
                response.staffKind(),
                response.email(),
                response.phone(),
                response.isActive(),
                response.roles()
        );
    }
}

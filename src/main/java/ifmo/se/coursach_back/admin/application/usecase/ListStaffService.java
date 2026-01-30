package ifmo.se.coursach_back.admin.application.usecase;

import ifmo.se.coursach_back.admin.api.dto.AdminStaffSummaryResponse;
import ifmo.se.coursach_back.admin.application.AdminAccountService;
import ifmo.se.coursach_back.admin.application.result.StaffSummaryResult;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Implementation of ListStaffUseCase that delegates to AdminAccountService.
 */
@Service
@RequiredArgsConstructor
public class ListStaffService implements ListStaffUseCase {
    private final AdminAccountService adminAccountService;

    @Override
    public List<StaffSummaryResult> execute(String role, String staffKind) {
        List<AdminStaffSummaryResponse> responses = adminAccountService.listStaff(role, staffKind);
        return responses.stream()
                .map(response -> new StaffSummaryResult(
                        response.staffId(),
                        response.accountId(),
                        response.fullName(),
                        response.staffKind(),
                        response.email(),
                        response.phone(),
                        response.isActive(),
                        response.roles()
                ))
                .toList();
    }
}

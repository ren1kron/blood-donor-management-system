package ifmo.se.coursach_back.admin.application.usecase;

import ifmo.se.coursach_back.admin.application.result.StaffSummaryResult;
import java.util.List;

/**
 * Use case interface for listing staff members.
 */
public interface ListStaffUseCase {
    /**
     * List all staff members, optionally filtered by role and staff kind.
     * @param role optional role filter
     * @param staffKind optional staff kind filter
     * @return list of staff summary results
     */
    List<StaffSummaryResult> execute(String role, String staffKind);
}

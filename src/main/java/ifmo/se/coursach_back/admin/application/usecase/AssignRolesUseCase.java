package ifmo.se.coursach_back.admin.application.usecase;

import ifmo.se.coursach_back.admin.application.command.AssignRolesCommand;
import ifmo.se.coursach_back.admin.application.result.StaffSummaryResult;

/**
 * Use case interface for assigning roles to an account.
 */
public interface AssignRolesUseCase {
    /**
     * Assign roles to an account.
     * @param command the command containing account ID and roles
     * @return the staff summary result after role assignment
     */
    StaffSummaryResult execute(AssignRolesCommand command);
}

package ifmo.se.coursach_back.admin.dto;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record AdminAssignRolesRequest(
        @NotEmpty List<String> roles
) {
}
